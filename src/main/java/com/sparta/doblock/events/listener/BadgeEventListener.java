package com.sparta.doblock.events.listener;

import com.sparta.doblock.events.entity.BadgeType;
import com.sparta.doblock.events.entity.Badges;
import com.sparta.doblock.events.entity.FeedEvents;
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
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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

    @Transactional
    @EventListener(classes = BadgeEvents.CompletedTodoBadgeEvent.class)
    public ResponseEntity<?> createTodoBadges(BadgeEvents.CompletedTodoBadgeEvent badgeEvents){

        long completedTodo = todoRepository.countAllByMemberAndCompleted(badgeEvents.getMemberDetails().getMember(), true);

        createBadges(completedTodo, 10L, BadgeType.COMPLETED_TODO_TEN, badgeEvents.getMemberDetails().getMember());

        createBadges(completedTodo, 50L, BadgeType.COMPLETED_TODO_FIFTY, badgeEvents.getMemberDetails().getMember());

        createBadges(completedTodo, 100L, BadgeType.COMPLETED_TODO_HUNDRED, badgeEvents.getMemberDetails().getMember());

        return null;
    }

    @Transactional
    @EventListener(classes = BadgeEvents.CreateFeedBadgeEvent.class)
    public ResponseEntity<?> createFeedBadges(BadgeEvents.CreateFeedBadgeEvent badgeEvents){

        long createdFeed = feedRepository.countAllByMember(badgeEvents.getMemberDetails().getMember());

        createBadges(createdFeed, 10L, BadgeType.CREATED_FEED_TEN, badgeEvents.getMemberDetails().getMember());

        createBadges(createdFeed, 50L, BadgeType.CREATED_FEED_FIFTY, badgeEvents.getMemberDetails().getMember());

        createBadges(createdFeed, 100L, BadgeType.CREATED_FEED_HUNDRED, badgeEvents.getMemberDetails().getMember());

        return null;
    }

    @Transactional
    @EventListener(classes = BadgeEvents.CreateReactionBadgeEvent.class)
    public ResponseEntity<?> createReactionBadges(BadgeEvents.CreateReactionBadgeEvent badgeEvents){

        long createdReaction = reactionRepository.countAllByMember(badgeEvents.getMemberDetails().getMember());

        createBadges(createdReaction, 10L, BadgeType.CREATED_REACTION_TEN, badgeEvents.getMemberDetails().getMember());

        createBadges(createdReaction, 50L, BadgeType.CREATED_REACTION_FIFTY, badgeEvents.getMemberDetails().getMember());

        createBadges(createdReaction, 100L, BadgeType.CREATED_REACTION_HUNDRED, badgeEvents.getMemberDetails().getMember());

        return null;
    }

    @Transactional
    @EventListener(classes = BadgeEvents.CreateCommentBadgeEvent.class)
    public ResponseEntity<?> createCommentBadges(BadgeEvents.CreateCommentBadgeEvent badgeEvents){

        long createdComment = commentRepository.countAllByMember(badgeEvents.getMemberDetails().getMember());

        createBadges(createdComment, 10L, BadgeType.CREATED_COMMENT_TEN, badgeEvents.getMemberDetails().getMember());

        createBadges(createdComment, 50L, BadgeType.CREATED_COMMENT_FIFTY, badgeEvents.getMemberDetails().getMember());

        createBadges(createdComment, 100L, BadgeType.CREATED_COMMENT_HUNDRED, badgeEvents.getMemberDetails().getMember());

        return null;
    }

    @Transactional
    @EventListener(classes = BadgeEvents.FollowToMemberBadgeEvent.class)
    public ResponseEntity<?> createFollowingBadges(BadgeEvents.FollowToMemberBadgeEvent badgeEvents){

        long followToMember = followRepository.countAllByFromMember(badgeEvents.getMemberDetails().getMember());

        createBadges(followToMember, 10L, BadgeType.FOLLOW_TO_MEMBER_TEN, badgeEvents.getMemberDetails().getMember());

        createBadges(followToMember, 50L, BadgeType.FOLLOW_TO_MEMBER_FIFTY, badgeEvents.getMemberDetails().getMember());

        createBadges(followToMember, 100L, BadgeType.FOLLOW_TO_MEMBER_HUNDRED, badgeEvents.getMemberDetails().getMember());

        return null;
    }

    @Transactional
    public ResponseEntity<?> createBadges(Long count, Long limit, BadgeType badgeType, Member member){

        if(Objects.equals(count, limit) && !badgesRepository.existsByBadgeTypeAndMember(badgeType, member)){

            Badges badges = Badges.builder()
                    .member(member)
                    .badgeType(badgeType)
                    .build();

            badgesRepository.save(badges);

            applicationEventPublisher.publishEvent(new FeedEvents(badgeType, member));

            return ResponseEntity.ok("새로운 뱃지를 획득하셨습니다!");
        }else return null;
    }
}
