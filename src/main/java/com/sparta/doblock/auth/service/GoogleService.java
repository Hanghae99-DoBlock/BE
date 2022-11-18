package com.sparta.doblock.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.doblock.auth.dto.GoogleProfileDto;
import com.sparta.doblock.member.entity.Authority;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.member.repository.MemberRepository;
import com.sparta.doblock.security.token.TokenDto;
import com.sparta.doblock.util.LoginUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginUtil loginUtil;

    @Value("${google.tokenUri}")
    private String googleTokenUrl;

    @Value("${google.clientId}")
    private String googleClientId;

    @Value("${google.redirectUri}")
    private String googleCallbackUrl;

    @Value("${google.clientSecret}")
    private String googleClientSecret;

    @Value("${google.userInfoUri}")
    private String googleProfileUrl;

    public ResponseEntity<?> login(String code) throws JsonProcessingException {

        String oauthToken = getAccessToken(code);

        Member member = saveUser(oauthToken);

        loginUtil.forceLogin(member);

        TokenDto tokenDto = loginUtil.generateToken(member);

        HttpHeaders httpHeaders = loginUtil.setHttpHeaders(tokenDto);

        return ResponseEntity.ok().headers(httpHeaders).body("로그인 완료!");
    }

    public String getAccessToken(String code) throws JsonProcessingException{

        HttpHeaders headers = new HttpHeaders();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("code", code);
        params.add("redirect_uri", googleCallbackUrl);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> googleTokenRequest = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(googleTokenUrl,googleTokenRequest,String.class);

        String responseBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        return jsonNode.get("access_token").asText();
    }

    @Transactional
    public Member saveUser(String oauthToken) throws JsonProcessingException {

        GoogleProfileDto profile = findProfile(oauthToken);

        Optional<Member> googleMember = memberRepository.findBySocialId(profile.getGoogleMemberId());

        if(googleMember.isEmpty()) {

            if (memberRepository.existsByEmail(profile.getEmail())){
                throw new RuntimeException("이미 사용 중인 이메일입니다.");
            }

            Member member = Member.builder()
                    .email(profile.getEmail())
                    .nickname(profile.getNickname())
                    .socialId(profile.getGoogleMemberId())
                    .socialCode("GOOGLE")
                    .profileImage(profile.getProfileImage())
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .authority(Authority.ROLE_SOCIAL)
                    .build();

            memberRepository.save(member);

            return member;

        } else return googleMember.get();
    }

    public GoogleProfileDto findProfile(String oauthToken) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + oauthToken);

        HttpEntity<MultiValueMap<String, String>> googleProfileRequest = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(googleProfileUrl,googleProfileRequest,String.class);

        String responseBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String googleMemberId = jsonNode.get("sub").asText();
        String email = jsonNode.get("email").asText();
        String nickname = jsonNode.get("name").asText();
        String profileImage = jsonNode.get("picture").asText();

        return GoogleProfileDto.builder()
                .googleMemberId(googleMemberId)
                .email(email)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }
}
