package com.sparta.doblock.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.doblock.auth.dto.KakaoProfile;
import com.sparta.doblock.member.entity.Authority;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.member.repository.MemberRepository;
import com.sparta.doblock.security.TokenProvider;
import com.sparta.doblock.security.token.RefreshToken;
import com.sparta.doblock.security.token.RefreshTokenRepository;
import com.sparta.doblock.security.token.TokenDto;
import com.sparta.doblock.util.HeaderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final HeaderUtil headerUtil;

    @Value("${kakao.tokenUri}")
    private String KAKAO_SNS_LOGIN_URL;

    @Value("${kakao.clientId}")
    private String KAKAO_SNS_CLIENT_ID;

    @Value("${kakao.redirectUri}")
    private String KAKAO_SNS_CALLBACK_URL;

    @Value("${kakao.userInfoUri}")
    private String KAKAO_SNS_User_URL;

    public ResponseEntity<?> login(String code) throws JsonProcessingException {

        String oauthToken = getAccessToken(code);

        Member member = saveUser(oauthToken);

        TokenDto tokenDto = generateToken(member);

        HttpHeaders httpHeaders = headerUtil.getHttpHeaders(tokenDto);

        return ResponseEntity.ok().headers(httpHeaders).body("로그인 완료!");
    }

    public String getAccessToken(String code) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_SNS_CLIENT_ID);
        params.add("redirect_uri", KAKAO_SNS_CALLBACK_URL);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(KAKAO_SNS_LOGIN_URL,kakaoTokenRequest,String.class);

        String responseBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    @Transactional
    public Member saveUser(String oauthToken) throws JsonProcessingException {

        KakaoProfile profile = findProfile(oauthToken);

        Optional<Member> kakaoMember = memberRepository.findBySocialId(profile.getKakaoMemberId().toString());

        if(kakaoMember.isEmpty()) {

            if (memberRepository.existsByEmail(profile.getEmail())){
                throw new RuntimeException("이미 사용 중인 이메일입니다.");
            }

            Member member = Member.builder()
                    .email(profile.getEmail())
                    .nickname(profile.getNickname())
                    .socialId(profile.getKakaoMemberId().toString())
                    .socialCode("KAKAO")
                    .profileImage(profile.getProfileImage())
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .authority(Authority.ROLE_SOCIAL)
                    .build();

            memberRepository.save(member);

            return member;

        } else return kakaoMember.get();
    }

    public KakaoProfile findProfile(String oauthToken) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + oauthToken); //(1-4)

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(KAKAO_SNS_User_URL,kakaoProfileRequest,String.class);

        String responseBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        Long kakaoMemberId = jsonNode.get("id").asLong();
        String email = jsonNode.get("kakao_account").get("email").asText();
        String nickname = jsonNode.get("kakao_account").get("profile").get("nickname").asText();
        String profileImage = jsonNode.get("kakao_account").get("profile").get("profile_image_url").asText();

        return KakaoProfile.builder()
                .kakaoMemberId(kakaoMemberId)
                .email(email)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }

    @Transactional
    public TokenDto generateToken(Member member) {

        MemberDetailsImpl memberDetails = new MemberDetailsImpl(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetails, null, memberDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenDto tokenDto = tokenProvider.generateTokenDto(member);

        RefreshToken refreshToken = RefreshToken.builder()
                .key(member.getSocialId())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        return tokenDto;
    }
}
