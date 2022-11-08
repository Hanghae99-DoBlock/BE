package com.sparta.doblock.reaction.dto.request;

import com.sparta.doblock.reaction.entity.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@AllArgsConstructor
public class ReactionRequestDto {
    @Enumerated(EnumType.STRING)
    private ReactionType reactionType;
}
