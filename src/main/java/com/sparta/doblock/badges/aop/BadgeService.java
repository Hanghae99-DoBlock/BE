package com.sparta.doblock.badges.aop;

import com.sparta.doblock.badges.entity.BadgeType;
import com.sparta.doblock.badges.entity.Badges;
import com.sparta.doblock.badges.repository.BadgesRepository;
import com.sparta.doblock.comment.repository.CommentRepository;
import com.sparta.doblock.feed.repository.FeedRepository;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.profile.repository.FollowRepository;
import com.sparta.doblock.reaction.repository.ReactionRepository;
import com.sparta.doblock.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Aspect
@Component
@RequiredArgsConstructor
public class BadgeService {

    private final TodoRepository todoRepository;
    private final FeedRepository feedRepository;
    private final ReactionRepository reactionRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;
    private final BadgesRepository badgesRepository;

    @Transactional
    @AfterReturning("@annotation(com.sparta.doblock.badges.aop.TodoBadgeEvent) && args(memberDetails)")
    public ResponseEntity<?> createTodoBadges(JoinPoint joinPoint, MemberDetailsImpl memberDetails){

        long completedTodo = todoRepository.countByMemberAndCompleted(memberDetails.getMember(), true);

        System.out.println(completedTodo);
        System.out.println(badgesRepository.existsByBadgeTypeAndMember(BadgeType.COMPLETED_TODO_TEN, memberDetails.getMember()));

        if (completedTodo == 10 && !badgesRepository.existsByBadgeTypeAndMember(BadgeType.COMPLETED_TODO_TEN, memberDetails.getMember())){

            Badges badges = Badges.builder()
                    .member(memberDetails.getMember())
                    .badgeType(BadgeType.COMPLETED_TODO_TEN)
                    .build();

            badgesRepository.save(badges);

            return ResponseEntity.ok("새로운 뱃지를 획득하셨습니다!");
        }

        if (completedTodo == 50 && !badgesRepository.existsByBadgeTypeAndMember(BadgeType.COMPLETED_TODO_FIFTY, memberDetails.getMember())){

            Badges badges = Badges.builder()
                    .member(memberDetails.getMember())
                    .badgeType(BadgeType.COMPLETED_TODO_FIFTY)
                    .build();

            badgesRepository.save(badges);

            return ResponseEntity.ok("새로운 뱃지를 획득하셨습니다!");
        }

        if (completedTodo == 100 && !badgesRepository.existsByBadgeTypeAndMember(BadgeType.COMPLETED_TODO_HUNDRED, memberDetails.getMember())){

            Badges badges = Badges.builder()
                    .member(memberDetails.getMember())
                    .badgeType(BadgeType.COMPLETED_TODO_HUNDRED)
                    .build();

            badgesRepository.save(badges);

            return ResponseEntity.ok("새로운 뱃지를 획득하셨습니다!");
        }

        return null;
    }
}
