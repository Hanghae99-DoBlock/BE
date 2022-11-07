package com.sparta.doblock.profile.controller;

import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/profile")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/{member_id}/follow")
    public ResponseEntity<?> follow(@PathVariable(name = "member_id") Long memberId, @AuthenticationPrincipal MemberDetailsImpl memberDetailsimpl){
        return profileService.follow(memberId, memberDetailsimpl.getMember());
    }

    @GetMapping("/{member_id}/following")
    public ResponseEntity<?> getFollowingList(@PathVariable(name = "member_id") Long memberId, @AuthenticationPrincipal MemberDetailsImpl memberDetailsimpl){
        return profileService.getFollowingList(memberId, memberDetailsimpl.getMember());
    }

    @GetMapping("/{member_id}/follower")
    public ResponseEntity<?> getFollowerList(@PathVariable(name = "member_id") Long memberId, @AuthenticationPrincipal MemberDetailsImpl memberDetailsimpl){
        return profileService.getFollowerList(memberId, memberDetailsimpl.getMember());
    }
}
