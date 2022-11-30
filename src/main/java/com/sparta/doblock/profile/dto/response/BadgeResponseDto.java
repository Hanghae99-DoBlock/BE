package com.sparta.doblock.profile.dto.response;

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
}
