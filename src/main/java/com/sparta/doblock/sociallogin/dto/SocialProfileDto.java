package com.sparta.doblock.sociallogin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SocialProfileDto {

    private String socialMemberId;
    private String email;
    private String profileImage;
    private String nickname;
}
