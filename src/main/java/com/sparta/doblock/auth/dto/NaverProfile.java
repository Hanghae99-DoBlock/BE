package com.sparta.doblock.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NaverProfile {

    private String naverMemberId;
    private String email;
    private String profileImage;
    private String nickname;
}