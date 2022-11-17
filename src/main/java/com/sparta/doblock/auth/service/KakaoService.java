package com.sparta.doblock.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.doblock.auth.dto.KakaoProfile;
import com.sparta.doblock.auth.dto.OauthToken;
import com.sparta.doblock.member.dto.response.MemberResponseDto;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    private final ApplicationEventPublisher eventPublisher;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;
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

    @Transactional
    public ResponseEntity<MemberResponseDto> login(String code){

        // 인가코드로 토큰받기
        OauthToken oauthToken = getAccessToken(code);

        //토큰으로 유저정보
        Member member = saveUser(oauthToken.getAccessToken());

        // 토큰발급
        TokenDto tokenDto = generateToken(member);

        // 리턴할 헤더 제작
        HttpHeaders httpHeaders = headerUtil.getHttpHeaders(tokenDto);

        // 리턴할 바디 제작
        MemberResponseDto memberResponseDto = MemberResponseDto.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .build();

        return ResponseEntity.ok().headers(httpHeaders).body(memberResponseDto);
    }

    public OauthToken getAccessToken(String code) {
        //(3)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //(4)
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_SNS_CLIENT_ID);
        params.add("redirect_uri", KAKAO_SNS_CALLBACK_URL);
        params.add("code", code);

        //(5)
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        // 밑에 바꾼거 때매 그런가?
        ResponseEntity<String> tokenResponse1 = restTemplate.postForEntity(KAKAO_SNS_LOGIN_URL,kakaoTokenRequest,String.class);
        //(6)

        //(7)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OauthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(tokenResponse1.getBody(), OauthToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return oauthToken; //(8)
    }


    private Member saveUser(String access_token){
        KakaoProfile profile = findProfile(access_token);
        //(2)
        Optional<Member> checkmember = memberRepository.findBySocialId(profile.getId());
        //(3)
        if(checkmember.isEmpty()) {
            Member member = Member.builder()
                    .socialId(profile.getId())
                    .nickname(profile.getKakaoAccount().getProfile().getNickname())
                    .socialCode(profile.getId().substring(0,5)+ UUID.randomUUID().toString().charAt(0))
                    .email(profile.getKakaoAccount().getEmail())
                    .authority(Authority.ROLE_MEMBER)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .build();
            memberRepository.save(member);
            eventPublisher.publishEvent(member);
            return member;
        }
        else
            return checkmember.get();
    }

    private KakaoProfile findProfile(String token) {

        //(1-3)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token); //(1-4)
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //(1-5)
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
                new HttpEntity<>(headers);

        //(1-6)
        // Http 요청 (POST 방식) 후, response 변수에 응답을 받음
        ResponseEntity<String> kakaoProfileResponse = restTemplate.postForEntity(KAKAO_SNS_User_URL,kakaoProfileRequest,String.class);

        //(1-7)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper.readValue(kakaoProfileResponse.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return kakaoProfile;
    }

    public TokenDto generateToken(Member member) {
        MemberDetailsImpl memberDetails = new MemberDetailsImpl(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetails, null, memberDetails.getAuthorities());
        TokenDto tokenDto = tokenProvider.generateTokenDto((Member) authentication);

        RefreshToken refreshToken = RefreshToken.builder()
                .key(member.getSocialId())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);
        return tokenDto;
    }
}
