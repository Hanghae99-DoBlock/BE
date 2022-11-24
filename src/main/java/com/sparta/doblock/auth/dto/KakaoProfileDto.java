package com.sparta.doblock.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoProfileDto {

    private Long kakaoMemberId;
    private String email;
    private String profileImage;
    private String nickname;
}
