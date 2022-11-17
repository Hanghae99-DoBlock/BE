package com.sparta.doblock.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.doblock.auth.dto.GoogleProfile;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleService {

    private final ApplicationEventPublisher eventPublisher;
    private final HeaderUtil headerUtil;

    private final RefreshTokenRepository refreshTokenRepository;

    private final TokenProvider tokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    private final RestTemplate restTemplate;


    @Value("${google.tokenUri}")
    private String GOOGLE_SNS_LOGIN_URL;

    @Value("${google.clientId}")
    private String GOOGLE_SNS_CLIENT_ID;

    @Value("${google.redirectUri}")
    private String GOOGLE_SNS_CALLBACK_URL;

    @Value("${google.clientSecret}")
    private String GOOGLE_SNS_CLIENT_SECRET;

    @Value("${google.userInfoUri}")
    private String GOOGLE_SNS_User_URL;


    @Transactional
    public ResponseEntity<MemberResponseDto> login(String code){
        // 인가코드로 토큰받기
        OauthToken oauthToken = getAccessToken(code);
        // 토큰으로 사용자 정보 요청
        Member member = saveUser(oauthToken.getAccessToken());
        // 사용자 정보를 토대로 토큰발급
        TokenDto tokenDto = generateToken(member);
        // 리턴할 헤더 제작
        HttpHeaders httpHeaders = headerUtil.getHttpHeaders(tokenDto);
        // 리턴할 바디 제작
        MemberResponseDto memberResponseDto = MemberResponseDto.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .build();
        //리턴 바디 상태 코드 및 메세지 넣기
        return ResponseEntity.ok().headers(httpHeaders).body(memberResponseDto);
    }
    private OauthToken getAccessToken(String code) {
        String decodedCode = "";
        decodedCode = java.net.URLDecoder.decode(code, StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        //(4)
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", GOOGLE_SNS_CLIENT_ID);
        params.add("client_secret", GOOGLE_SNS_CLIENT_SECRET);
        params.add("code", decodedCode);
        params.add("redirect_uri", GOOGLE_SNS_CALLBACK_URL);
        params.add("grant_type", "authorization_code");
        //(5)
        HttpEntity<MultiValueMap<String, String>> googleTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> tokenResponse1 = restTemplate.postForEntity(GOOGLE_SNS_LOGIN_URL,googleTokenRequest,String.class);
        //(6)
        ObjectMapper objectMapper = new ObjectMapper();
        //(7)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OauthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(tokenResponse1.getBody(), OauthToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return oauthToken; //(8)
    }

    @Transactional
    public Member saveUser(String access_token){
        GoogleProfile profile = findProfile(access_token);
        //(2)
        Optional<Member> checkmember = memberRepository.findBySocialId(profile.getSub());
        //(3)
        if(checkmember.isEmpty()) {
            Member member = Member.builder()
                    .socialId(profile.getSub())
                    .socialCode(profile.getSub().substring(0,5)+UUID.randomUUID().toString().charAt(0))
                    .nickname(profile.getName())
                    .email(profile.getEmail())
                    .authority(Authority.ROLE_MEMBER)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .build();
            memberRepository.save(member);
            eventPublisher.publishEvent(member);
            return member;
        }
        return checkmember.get();
    }

    private GoogleProfile findProfile(String token) {
        //(1-3)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token); //(1-4)
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        //(1-5)
        HttpEntity<MultiValueMap<String, String>> googleProfileRequest =
                new HttpEntity<>(headers);
        ResponseEntity<String> googleProfileResponse = restTemplate.postForEntity(GOOGLE_SNS_User_URL,googleProfileRequest,String.class);
        //(1-7)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        GoogleProfile googleProfile = null;
        try {
            googleProfile = objectMapper.readValue(googleProfileResponse.getBody(), GoogleProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return googleProfile;
    }

    private TokenDto generateToken(Member member) {
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
