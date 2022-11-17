package com.sparta.doblock.auth.controller;

import com.sparta.doblock.auth.service.GoogleService;
import com.sparta.doblock.auth.service.KakaoService;
import com.sparta.doblock.member.dto.response.MemberResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/members/login")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final KakaoService kakaoService;
    private final GoogleService googleService;

    @GetMapping("/kakao") // (3)
    public ResponseEntity<MemberResponseDto> getkakaoLogin(@RequestParam("code") String code){//(4)
        return kakaoService.login(code);
    }

    @GetMapping("/google") // (3)
    public ResponseEntity<MemberResponseDto> getgoogleLogin(@RequestParam("code") String code){ //(4)
        return googleService.login(code);
    }
}
