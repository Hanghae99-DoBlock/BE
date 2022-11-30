package com.sparta.doblock.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FollowResponseDto {

    private Long memberId;
    private String profileImage;
    private String nickname;
    private String email;
    private boolean followOrNot;
}
