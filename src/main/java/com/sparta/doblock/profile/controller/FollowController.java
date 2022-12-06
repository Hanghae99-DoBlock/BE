package com.sparta.doblock.profile.controller;

import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.profile.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{memberId}/follow")
    public ResponseEntity<?> follow(@PathVariable(name = "memberId") Long memberId, @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        return followService.follow(memberId, memberDetails);
    }

    @GetMapping("/{memberId}/following")
    public ResponseEntity<?> getFollowingList(@PathVariable(name = "memberId") Long memberId, @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        return followService.getFollowingList(memberId, memberDetails);
    }

    @GetMapping("/{memberId}/follower")
    public ResponseEntity<?> getFollowerList(@PathVariable(name = "memberId") Long memberId, @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        return followService.getFollowerList(memberId, memberDetails);
    }
}
