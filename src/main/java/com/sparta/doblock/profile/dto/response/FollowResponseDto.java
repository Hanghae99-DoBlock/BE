package com.sparta.doblock.profile.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowResponseDto {

    private Long memberId;
    private String profileImage;
    private String nickname;
    private boolean followOrNot;
}
