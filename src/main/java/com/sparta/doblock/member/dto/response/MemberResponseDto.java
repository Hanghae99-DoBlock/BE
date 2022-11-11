package com.sparta.doblock.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberResponseDto {

    private Long memberId;
    private String profileImage;
    private String nickname;
}
