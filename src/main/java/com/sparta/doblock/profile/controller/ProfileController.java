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

    @PatchMapping("/edit")
    public ResponseEntity<?> editProfile(@ModelAttribute EditProfileRequestDto editProfileRequestDto, @AuthenticationPrincipal MemberDetailsImpl memberDetailsimpl){
        return profileService.editProfile(editProfileRequestDto, memberDetailsimpl.getMember());
    }

    @PostMapping("/{nickname}/follow")
    public ResponseEntity<?> follow(@PathVariable(name = "nickname") String nickname, @AuthenticationPrincipal MemberDetailsImpl memberDetailsimpl){
        return profileService.follow(nickname, memberDetailsimpl.getMember());
    }

    @GetMapping("/{nickname}/following")
    public ResponseEntity<?> getFollowingList(@PathVariable(name = "nickname") String nickname, @AuthenticationPrincipal MemberDetailsImpl memberDetailsimpl){
        return profileService.getFollowingList(nickname, memberDetailsimpl.getMember());
    }

    @GetMapping("/{nickname}/follower")
    public ResponseEntity<?> getFollowerList(@PathVariable(name = "nickname") String nickname, @AuthenticationPrincipal MemberDetailsImpl memberDetailsimpl){
        return profileService.getFollowerList(nickname, memberDetailsimpl.getMember());
    }
}
