package com.sparta.doblock.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.doblock.auth.dto.NaverProfile;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NaverService {

    private final ApplicationEventPublisher eventPublisher;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;

    // TODO update
    @Value("${spring.security.oauth2.client.provider.naver.tokenUri}")
    private String NAVER_SNS_LOGIN_URL;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String NAVER_SNS_CLIENT_ID;

    // TODO update
    @Value("${spring.security.oauth2.client.registration.naver.redirectUri}")
    private String NAVER_SNS_CALLBACK_URL;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String NAVER_SNS_CLIENT_SECRET;

    // TODO update
    @Value("${spring.security.oauth2.client.provider.naver.userInfoUri}")
    private String NAVER_SNS_User_URL;

    @Transactional
    public ResponseEntity<?> login(String code, String state) {
        OauthToken oauthToken = getAccessToken(code, state);

        Member member = saveUser(oauthToken.getAccessToken());

        TokenDto tokenDto = generateToken(member);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + tokenDto.getAccessToken());
        httpHeaders.set("RefreshToken", tokenDto.getRefreshToken());
        httpHeaders.set("Access-Token-Expire-Time", String.valueOf(tokenDto.getAccessTokenExpiresIn()));

        MemberResponseDto memberResponseDto = MemberResponseDto.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .build();

        return ResponseEntity.ok().headers(httpHeaders).body(memberResponseDto);
    }

    private OauthToken getAccessToken(String code, String state) {
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(NAVER_SNS_LOGIN_URL)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", NAVER_SNS_CLIENT_ID)
                .queryParam("code", code)
                .queryParam("state", state)
                .queryParam("client_secret", NAVER_SNS_CLIENT_SECRET)
                .queryParam("redirect_uri", NAVER_SNS_CALLBACK_URL)
                .build();

        ResponseEntity<String> resultEntity = restTemplate.postForEntity(builder.toUriString(), null, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return objectMapper.readValue(resultEntity.getBody(), OauthToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Member saveUser(String accessToken) {
        // TODO: profile image
        NaverProfile profile = findProfile(accessToken);

        Member member = memberRepository.findBySocialId(profile.getResponse().getId()).orElse(
                Member.builder()
                        .socialCode(UUID.randomUUID().toString().substring(0, 5))
                        .socialId(profile.getResponse().getId())
                        .nickname(profile.getResponse().getName())
                        .email(profile.getResponse().getEmail())
                        .authority(Authority.ROLE_SOCIAL)
                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .build()
        );
        memberRepository.save(member);

        return member;
    }

    private NaverProfile findProfile(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> naverProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<String> naverProfileResponse = restTemplate.postForEntity(NAVER_SNS_User_URL, naverProfileRequest, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return objectMapper.readValue(naverProfileResponse.getBody(), NaverProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private TokenDto generateToken(Member member) {
        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
        RefreshToken refreshToken = RefreshToken.builder()
                .key(member.getSocialId())
                .value(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        return tokenDto;
    }
}
