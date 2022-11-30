package com.sparta.doblock.reaction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReactionResponseDto {

    private Long memberId;
    private String profileImage;
    private String nickname;
    private String email;
    private String reactionType;
}
