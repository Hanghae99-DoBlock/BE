package com.sparta.doblock.events.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.doblock.events.service.EasterEggService;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/egg")
public class EasterEggController {

    private final EasterEggService easterEggService;

    @GetMapping("/ready")
    public ResponseEntity<?> paymentReady(@AuthenticationPrincipal MemberDetailsImpl memberDetails) throws JsonProcessingException {
        return easterEggService.paymentReady(memberDetails);
    }

    @GetMapping("/approval")
    public ResponseEntity<?> paymentApproval(@RequestParam(name = "pg_token") String pgToken, @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        return easterEggService.paymentApproval(pgToken, memberDetails);
    }
}
