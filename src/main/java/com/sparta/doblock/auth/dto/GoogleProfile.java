package com.sparta.doblock.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoogleProfile {

    private String googleMemberId;
    private String email;
    private String profileImage;
    private String nickname;
}
