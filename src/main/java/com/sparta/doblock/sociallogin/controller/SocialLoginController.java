package com.sparta.doblock.sociallogin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.doblock.sociallogin.service.GoogleService;
import com.sparta.doblock.sociallogin.service.KakaoService;
import com.sparta.doblock.sociallogin.service.NaverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/login")
public class SocialLoginController {

    private final KakaoService kakaoService;
    private final GoogleService googleService;
    private final NaverService naverService;

    @GetMapping("/kakao")
    public ResponseEntity<?> getKakaoLogin(@RequestParam String code) throws JsonProcessingException {
        return kakaoService.login(code);
    }

    @GetMapping("/google")
    public ResponseEntity<?> getGoogleLogin(@RequestParam String code) throws JsonProcessingException {
        return googleService.login(code);
    }

    @GetMapping("/naver")
    public ResponseEntity<?> getNaverLogin(@RequestParam String code, @RequestParam String state) throws JsonProcessingException {
        return naverService.login(code, state);
    }
}
