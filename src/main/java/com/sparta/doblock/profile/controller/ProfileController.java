package com.sparta.doblock.profile.controller;

import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.profile.dto.request.EditProfileRequestDto;
import com.sparta.doblock.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/profile")
public class ProfileController {

    private final ProfileService profileService;

    @PatchMapping("/edit")
    public ResponseEntity<?> editProfile(@ModelAttribute @Valid EditProfileRequestDto editProfileRequestDto, @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        return profileService.editProfile(editProfileRequestDto, memberDetails);
    }

    @PostMapping("/{nickname}/follow")
    public ResponseEntity<?> follow(@PathVariable(name = "nickname") String nickname, @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        return profileService.follow(nickname, memberDetails);
    }

    @GetMapping("/{nickname}/following")
    public ResponseEntity<?> getFollowingList(@PathVariable(name = "nickname") String nickname, @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        return profileService.getFollowingList(nickname, memberDetails);
    }

    @GetMapping("/{nickname}/follower")
    public ResponseEntity<?> getFollowerList(@PathVariable(name = "nickname") String nickname, @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        return profileService.getFollowerList(nickname, memberDetails);
    }
}
