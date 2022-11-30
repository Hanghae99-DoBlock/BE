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

        this.completedTodoThree = badgeTypes.contains(BadgeType.COMPLETED_TODO_THREE);
        this.completedTodoThirty = badgeTypes.contains(BadgeType.COMPLETED_TODO_THIRTY);
        this.completedTodoFifty = badgeTypes.contains(BadgeType.COMPLETED_TODO_FIFTY);
        this.createdFeedOne = badgeTypes.contains(BadgeType.CREATED_FEED_ONE);
        this.createdFeedThirty = badgeTypes.contains(BadgeType.CREATED_FEED_THIRTY);
        this.createdFeedFifty = badgeTypes.contains(BadgeType.CREATED_FEED_FIFTY);
        this.socialActiveOne = badgeTypes.contains(BadgeType.SOCIAL_ACTIVE_ONE);
        this.socialActiveThirty = badgeTypes.contains(BadgeType.SOCIAL_ACTIVE_THIRTY);
        this.socialActiveFifty = badgeTypes.contains(BadgeType.SOCIAL_ACTIVE_FIFTY);
        this.followToMemberSeven = badgeTypes.contains(BadgeType.FOLLOW_TO_MEMBER_SEVEN);
        this.followToMemberFifty = badgeTypes.contains(BadgeType.FOLLOW_TO_MEMBER_FIFTY);
        this.followToMemberHundredFifty = badgeTypes.contains(BadgeType.FOLLOW_TO_MEMBER_HUNDRED_FIFTY);
    }
}
