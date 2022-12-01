package com.sparta.doblock.profile.controller;

import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.profile.dto.request.BadgesRequestDto;
import com.sparta.doblock.profile.service.BadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/profile")
public class BadgeController {

    private final BadgeService badgeservice;

    @GetMapping("/{memberId}/badgelist")
    public ResponseEntity<?> getBadgeList(@PathVariable(name = "memberId") Long memberId, @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        return badgeservice.getBadgeList(memberId, memberDetails);
    }

    @GetMapping("/{memberId}/badges")
    public ResponseEntity<?> getBadges(@PathVariable(name = "memberId") Long memberId, @RequestParam String badgetype,
                                       @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        return badgeservice.getBadges(memberId, badgetype, memberDetails);
    }

    @PatchMapping("/edit/badges")
    public ResponseEntity<?> editBadges(@RequestBody BadgesRequestDto badgesRequestDto, @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        return badgeservice.editBadges(badgesRequestDto, memberDetails);
    }
}
