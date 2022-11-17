package com.sparta.doblock.events.entity;

import com.sparta.doblock.member.entity.MemberDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class BadgeEvents {

    @Getter
    @AllArgsConstructor
    public static class CompletedTodoBadgeEvent {

        private MemberDetailsImpl memberDetails;
    }

    @Getter
    @AllArgsConstructor
    public static class CreateFeedBadgeEvent{

        private MemberDetailsImpl memberDetails;
    }

    @Getter
    @AllArgsConstructor
    public static class CreateReactionBadgeEvent{

        private MemberDetailsImpl memberDetails;
    }

    @Getter
    @AllArgsConstructor
    public static class CreateCommentBadgeEvent{

        private MemberDetailsImpl memberDetails;
    }

    @Getter
    @AllArgsConstructor
    public static class FollowToMemberBadgeEvent{

        private MemberDetailsImpl memberDetails;
    }
}
