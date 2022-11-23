package com.sparta.doblock.feed.dto.request;

import com.sparta.doblock.events.entity.BadgeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventFeedRequestDto {

    private BadgeType badgeType;
}
