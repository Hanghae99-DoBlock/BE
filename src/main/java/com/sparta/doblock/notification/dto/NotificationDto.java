package com.sparta.doblock.notification.dto;

import com.sparta.doblock.events.entity.BadgeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NotificationDto {

    private String message;
    private BadgeType badgeType;
}
