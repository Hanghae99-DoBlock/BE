package com.sparta.doblock.reaction.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReactionRequestDto {
    private String type;

    public void capitalize() {
        this.type = type.toUpperCase();
    }
}
