package com.sparta.doblock.events.listener;

import com.sparta.doblock.events.entity.BadgeType;
import com.sparta.doblock.events.entity.Badges;
import com.sparta.doblock.events.repository.BadgesRepository;
import com.sparta.doblock.comment.repository.CommentRepository;
import com.sparta.doblock.events.entity.BadgeEvents;
import com.sparta.doblock.feed.repository.FeedRepository;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.profile.repository.FollowRepository;
import com.sparta.doblock.reaction.repository.ReactionRepository;
import com.sparta.doblock.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.transaction.Transactional;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class BadgeEventListener {

    private final TodoRepository todoRepository;
    private final FeedRepository feedRepository;
    private final ReactionRepository reactionRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;
    private final BadgesRepository badgesRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Async
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @TransactionalEventListener(classes = BadgeEvents.CompletedTodoBadgeEvent.class)
    public void createTodoBadges(BadgeEvents.CompletedTodoBadgeEvent badgeEvents){

        long completedTodo = todoRepository.countAllByMemberAndCompleted(badgeEvents.getMemberDetails().getMember(), true);

        createBadges(completedTodo, 3L, BadgeType.COMPLETED_TODO_THREE, badgeEvents.getMemberDetails().getMember());

        createBadges(completedTodo, 30L, BadgeType.COMPLETED_TODO_THIRTY, badgeEvents.getMemberDetails().getMember());

        createBadges(completedTodo, 50L, BadgeType.COMPLETED_TODO_FIFTY, badgeEvents.getMemberDetails().getMember());
    }

    @Async
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @TransactionalEventListener(classes = BadgeEvents.CreateFeedBadgeEvent.class)
    public void createFeedBadges(BadgeEvents.CreateFeedBadgeEvent badgeEvents){

        long createdFeed = feedRepository.countAllByMember(badgeEvents.getMemberDetails().getMember());

        createBadges(createdFeed, 1L, BadgeType.CREATED_FEED_ONE, badgeEvents.getMemberDetails().getMember());

        createBadges(createdFeed, 30L, BadgeType.CREATED_FEED_THIRTY, badgeEvents.getMemberDetails().getMember());

        createBadges(createdFeed, 50L, BadgeType.CREATED_FEED_FIFTY, badgeEvents.getMemberDetails().getMember());
    }

    @Async
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @TransactionalEventListener(classes = BadgeEvents.SocialActiveBadgeEvent.class)
    public void createReactionBadges(BadgeEvents.SocialActiveBadgeEvent badgeEvents){

        long createdReaction = reactionRepository.countAllByMember(badgeEvents.getMemberDetails().getMember());
        long createdComment = commentRepository.countAllByMember(badgeEvents.getMemberDetails().getMember());
        long socialActive = createdReaction + createdComment;

        createBadges(socialActive, 1L, BadgeType.SOCIAL_ACTIVE_ONE, badgeEvents.getMemberDetails().getMember());

        createBadges(socialActive, 30L, BadgeType.SOCIAL_ACTIVE_THIRTY, badgeEvents.getMemberDetails().getMember());

        createBadges(socialActive, 50L, BadgeType.SOCIAL_ACTIVE_FIFTY, badgeEvents.getMemberDetails().getMember());
    }

    @Async
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @TransactionalEventListener(classes = BadgeEvents.FollowToMemberBadgeEvent.class)
    public void createFollowingBadges(BadgeEvents.FollowToMemberBadgeEvent badgeEvents){

        long followToMember = followRepository.countAllByFromMember(badgeEvents.getMemberDetails().getMember());

        createBadges(followToMember, 7L, BadgeType.FOLLOW_TO_MEMBER_SEVEN, badgeEvents.getMemberDetails().getMember());

        createBadges(followToMember, 50L, BadgeType.FOLLOW_TO_MEMBER_FIFTY, badgeEvents.getMemberDetails().getMember());

        createBadges(followToMember, 150L, BadgeType.FOLLOW_TO_MEMBER_HUNDRED_FIFTY, badgeEvents.getMemberDetails().getMember());
    }

    @Transactional
    public void createBadges(Long count, Long limit, BadgeType badgeType, Member member){

        if(Objects.equals(count, limit) && !badgesRepository.existsByBadgeTypeAndMember(badgeType, member)){

            Badges badges = Badges.builder()
                    .member(member)
                    .badgeType(badgeType)
                    .build();

            badgesRepository.save(badges);

            applicationEventPublisher.publishEvent(new BadgeEvents.CreateBadgeEvent(badgeType, member));
        }
    }
}
