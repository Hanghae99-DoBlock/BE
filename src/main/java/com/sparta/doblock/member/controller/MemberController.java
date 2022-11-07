package com.sparta.doblock.member.controller;

import com.sparta.doblock.member.dto.request.MemberRequestDto;
import com.sparta.doblock.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/api/members/signup")
    public ResponseEntity<?> signup(@RequestBody MemberRequestDto memberRequestDto){
        return memberService.signup(memberRequestDto);
    }

    @PostMapping("/api/members/login")
    public ResponseEntity<?> login(@RequestBody MemberRequestDto memberRequestDto, HttpServletResponse httpServletResponse){
        return memberService.login(memberRequestDto, httpServletResponse);
    }
}
