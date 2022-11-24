package com.sparta.doblock.member.controller;

import com.sparta.doblock.member.dto.request.MemberRequestDto;
import com.sparta.doblock.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid MemberRequestDto memberRequestDto){
        return memberService.signup(memberRequestDto);
    }

    @PostMapping("/checkmail")
    public ResponseEntity<?> checkEmail(@RequestBody(required = false) @Valid MemberRequestDto memberRequestDto){
        return memberService.checkEmail(memberRequestDto);
    }

    @PostMapping("/checkname")
    public ResponseEntity<?> checkNickname(@RequestBody(required = false) @Valid MemberRequestDto memberRequestDto){
        return memberService.checkNickname(memberRequestDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody(required = false) @Valid MemberRequestDto memberRequestDto){
        return memberService.login(memberRequestDto);
    }
}
