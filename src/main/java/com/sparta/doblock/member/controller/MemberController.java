package com.sparta.doblock.member.controller;

import com.sparta.doblock.member.dto.request.MemberRequestDto;
import com.sparta.doblock.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody MemberRequestDto memberRequestDto){
        return memberService.signup(memberRequestDto);
    }
    @GetMapping("/checkmail")
    public ResponseEntity<?> checkEmail(@RequestBody MemberRequestDto memberRequestDto){
        return memberService.checkEmail(memberRequestDto);
    }

    @GetMapping("/checkname")
    public ResponseEntity<?> checkNickname(@RequestBody MemberRequestDto memberRequestDto){
        return memberService.checkNickname(memberRequestDto);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberRequestDto memberRequestDto, HttpServletResponse httpServletResponse){
        return memberService.login(memberRequestDto, httpServletResponse);
    }
}
