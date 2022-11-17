package com.sparta.doblock.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.doblock.auth.dto.NaverProfile;
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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NaverService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final HeaderUtil headerUtil;

    @Value("${naver.tokenUri}")
    private String NAVER_SNS_LOGIN_URL;

    @Value("${naver.clientId}")
    private String NAVER_SNS_CLIENT_ID;

    @Value("${naver.redirectUri}")
    private String NAVER_SNS_CALLBACK_URL;

    @Value("${naver.clientSecret}")
    private String NAVER_SNS_CLIENT_SECRET;

    @Value("${naver.userInfoUri}")
    private String NAVER_SNS_User_URL;

    public ResponseEntity<?> login(String code, String state) throws JsonProcessingException {

        String oauthToken = getAccessToken(code, state);

        Member member = saveUser(oauthToken);

        TokenDto tokenDto = generateToken(member);

        HttpHeaders httpHeaders = headerUtil.getHttpHeaders(tokenDto);

        return ResponseEntity.ok().headers(httpHeaders).body("로그인 완료!");
    }

    private String getAccessToken(String code, String state) throws JsonProcessingException {

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(NAVER_SNS_LOGIN_URL)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", NAVER_SNS_CLIENT_ID)
                .queryParam("code", code)
                .queryParam("state", state)
                .queryParam("client_secret", NAVER_SNS_CLIENT_SECRET)
                .queryParam("redirect_uri", NAVER_SNS_CALLBACK_URL)
                .build();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(builder.toUriString(), null, String.class);

        String responseBody = responseEntity.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    @Transactional
    public Member saveUser(String accessToken) throws JsonProcessingException {

        NaverProfile profile = findProfile(accessToken);

        Optional<Member> naverMember = memberRepository.findBySocialId(profile.getNaverMemberId());

        if(naverMember.isEmpty()){

            if (memberRepository.existsByEmail(profile.getEmail())){
                throw new RuntimeException("이미 사용 중인 이메일입니다.");
            }

            Member member = Member.builder()
                    .email(profile.getEmail())
                    .nickname(profile.getNickname())
                    .socialId(profile.getNaverMemberId())
                    .socialCode("NAVER")
                    .profileImage(profile.getProfileImage())
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .authority(Authority.ROLE_SOCIAL)
                    .build();

            memberRepository.save(member);

            return member;
        } else return naverMember.get();
    }

    public NaverProfile findProfile(String token) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/json");

        HttpEntity<MultiValueMap<String, String>> naverProfileRequest = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(NAVER_SNS_User_URL, naverProfileRequest, String.class);

        String responseBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        System.out.println(responseBody);

        String naverMemberId = jsonNode.get("response").get("id").asText();
        String email = jsonNode.get("response").get("email").asText();
        String nickname = jsonNode.get("response").get("nickname").asText();
        String profileImage = jsonNode.get("response").get("profile_image").asText();

        return NaverProfile.builder()
                .naverMemberId(naverMemberId)
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
