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

        createBadges(completedTodo, 10L, BadgeType.COMPLETED_TODO_TEN, badgeEvents.getMemberDetails().getMember());

        createBadges(completedTodo, 50L, BadgeType.COMPLETED_TODO_FIFTY, badgeEvents.getMemberDetails().getMember());

        createBadges(completedTodo, 100L, BadgeType.COMPLETED_TODO_HUNDRED, badgeEvents.getMemberDetails().getMember());
    }

    @Async
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @TransactionalEventListener(classes = BadgeEvents.CreateFeedBadgeEvent.class)
    public void createFeedBadges(BadgeEvents.CreateFeedBadgeEvent badgeEvents){

        long createdFeed = feedRepository.countAllByMember(badgeEvents.getMemberDetails().getMember());

        createBadges(createdFeed, 10L, BadgeType.CREATED_FEED_TEN, badgeEvents.getMemberDetails().getMember());

        createBadges(createdFeed, 50L, BadgeType.CREATED_FEED_FIFTY, badgeEvents.getMemberDetails().getMember());

        createBadges(createdFeed, 100L, BadgeType.CREATED_FEED_HUNDRED, badgeEvents.getMemberDetails().getMember());
    }

    @Async
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @TransactionalEventListener(classes = BadgeEvents.SocialActiveBadgeEvent.class)
    public void createReactionBadges(BadgeEvents.SocialActiveBadgeEvent badgeEvents){

        long createdReaction = reactionRepository.countAllByMember(badgeEvents.getMemberDetails().getMember());
        long createdComment = commentRepository.countAllByMember(badgeEvents.getMemberDetails().getMember());
        long socialActive = createdReaction + createdComment;

        createBadges(socialActive, 10L, BadgeType.SOCIAL_ACTIVE_TEN, badgeEvents.getMemberDetails().getMember());

        createBadges(socialActive, 50L, BadgeType.SOCIAL_ACTIVE_FIFTY, badgeEvents.getMemberDetails().getMember());

        createBadges(socialActive, 100L, BadgeType.SOCIAL_ACTIVE_HUNDRED, badgeEvents.getMemberDetails().getMember());
    }

    @Async
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @TransactionalEventListener(classes = BadgeEvents.FollowToMemberBadgeEvent.class)
    public void createFollowingBadges(BadgeEvents.FollowToMemberBadgeEvent badgeEvents){

        long followToMember = followRepository.countAllByFromMember(badgeEvents.getMemberDetails().getMember());

        createBadges(followToMember, 10L, BadgeType.FOLLOW_TO_MEMBER_TEN, badgeEvents.getMemberDetails().getMember());

        createBadges(followToMember, 50L, BadgeType.FOLLOW_TO_MEMBER_FIFTY, badgeEvents.getMemberDetails().getMember());

        createBadges(followToMember, 100L, BadgeType.FOLLOW_TO_MEMBER_HUNDRED, badgeEvents.getMemberDetails().getMember());
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
