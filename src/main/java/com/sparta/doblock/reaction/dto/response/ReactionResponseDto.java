package com.sparta.doblock.reaction.dto.response;

import com.sparta.doblock.reaction.entity.ReactionType;
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
    private ReactionType reactionType;
}
