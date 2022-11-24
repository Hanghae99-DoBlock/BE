package com.sparta.doblock.reaction.dto.request;

import com.sparta.doblock.reaction.entity.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReactionRequestDto {

    private ReactionType reactionType;
}
