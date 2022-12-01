package com.sparta.doblock.profile.dto.response;

import com.sparta.doblock.events.entity.BadgeType;
import com.sparta.doblock.events.entity.Badges;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class BadgeListResponseDto {

    private String badgeName;
    private String badgeImage;
    private boolean completedTodoThree;
    private boolean completedTodoThirty;
    private boolean completedTodoFifty;
    private boolean createdFeedOne;
    private boolean createdFeedThirty;
    private boolean createdFeedFifty;
    private boolean socialActiveOne;
    private boolean socialActiveThirty;
    private boolean socialActiveFifty;
    private boolean followToMemberSeven;
    private boolean followToMemberFifty;
    private boolean followToMemberHundredFifty;

    public void editBadgeListDto(List<Badges> badgesList){
        HashSet<BadgeType> badgeTypes = new HashSet<>();

        for (Badges badges : badgesList){
            badgeTypes.add(badges.getBadgeType());
        }

        this.completedTodoThree = badgeTypes.contains(BadgeType.CTT);
        this.completedTodoThirty = badgeTypes.contains(BadgeType.CTTY);
        this.completedTodoFifty = badgeTypes.contains(BadgeType.CTF);
        this.createdFeedOne = badgeTypes.contains(BadgeType.CFO);
        this.createdFeedThirty = badgeTypes.contains(BadgeType.CFT);
        this.createdFeedFifty = badgeTypes.contains(BadgeType.CFF);
        this.socialActiveOne = badgeTypes.contains(BadgeType.SAO);
        this.socialActiveThirty = badgeTypes.contains(BadgeType.SAT);
        this.socialActiveFifty = badgeTypes.contains(BadgeType.SAF);
        this.followToMemberSeven = badgeTypes.contains(BadgeType.FMS);
        this.followToMemberFifty = badgeTypes.contains(BadgeType.FMF);
        this.followToMemberHundredFifty = badgeTypes.contains(BadgeType.FMH);
    }
}
