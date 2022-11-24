package com.sparta.doblock.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.doblock.auth.service.GoogleService;
import com.sparta.doblock.auth.service.KakaoService;
import com.sparta.doblock.auth.service.NaverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/login")
public class AuthController {

    private final KakaoService kakaoService;
    private final GoogleService googleService;
    private final NaverService naverService;

    @GetMapping("/kakao")
    public ResponseEntity<?> getKakaoLogin(@RequestParam String code) throws JsonProcessingException {//(4)
        return kakaoService.login(code);
    }

    @GetMapping("/google")
    public ResponseEntity<?> getGoogleLogin(@RequestParam String code) throws JsonProcessingException { //(4)
        return googleService.login(code);
    }

    @GetMapping("/naver")
    public ResponseEntity<?> getNaverLogin(@RequestParam String code, @RequestParam String state) throws JsonProcessingException { //(4)
        return naverService.login(code, state);
    }
}
