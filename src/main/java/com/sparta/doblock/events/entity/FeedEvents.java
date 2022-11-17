package com.sparta.doblock.events.entity;

import com.sparta.doblock.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeedEvents {

    private BadgeType badgeType;
    private Member member;
}
