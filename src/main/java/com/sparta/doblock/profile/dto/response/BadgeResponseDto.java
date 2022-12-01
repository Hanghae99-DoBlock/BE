package com.sparta.doblock.profile.dto.response;

import com.sparta.doblock.events.entity.BadgeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BadgeResponseDto {

    private String badgeName;
    private String badgeImage;
    private String badgeDetail;
    private BadgeType badgeType;
    private boolean obtainedBadge;
}
