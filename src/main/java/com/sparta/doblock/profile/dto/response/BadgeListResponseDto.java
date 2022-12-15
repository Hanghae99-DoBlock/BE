package com.sparta.doblock.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class BadgeListResponseDto {

    private String badgeName;
    private String badgeImage;
    private List<BadgeResponseDto> badgeResponseDtoList;
}
