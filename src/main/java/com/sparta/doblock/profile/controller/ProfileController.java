package com.sparta.doblock.profile.controller;

import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.profile.dto.request.EditPasswordRequestDto;
import com.sparta.doblock.profile.dto.request.EditProfileRequestDto;
import com.sparta.doblock.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{memberId}")
    public ResponseEntity<?> getProfile(@PathVariable(name = "memberId") Long memberId, @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        return profileService.getProfile(memberId, memberDetails);
    }

    @PatchMapping("/edit")
    public ResponseEntity<?> editProfile(@ModelAttribute @Valid EditProfileRequestDto editProfileRequestDto,
                                         @AuthenticationPrincipal MemberDetailsImpl memberDetails) throws IllegalAccessException, IOException {
        return profileService.editProfile(editProfileRequestDto, memberDetails);
    }

    @PatchMapping("/edit/password")
    public ResponseEntity<?> editPassword(@RequestBody @Valid EditPasswordRequestDto editPasswordRequestDto,
                                          @AuthenticationPrincipal MemberDetailsImpl memberDetails) throws IllegalAccessException {
        return profileService.editPassword(editPasswordRequestDto, memberDetails);
    }
}
