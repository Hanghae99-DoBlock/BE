package com.sparta.doblock.profile.dto.request;

import com.sparta.doblock.events.entity.BadgeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BadgesRequestDto {

    private BadgeType badgeType;
}
