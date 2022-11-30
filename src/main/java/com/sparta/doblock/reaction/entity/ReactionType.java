package com.sparta.doblock.reaction.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReactionType {

    LIKE("&#x1F44D"),
    HEART("&#x2764"),
    SMILE("&#x1F60A"),
    PARTY("&#x1F389"),
    FIRE("&#x1F525");

    private final String emoji;
}
