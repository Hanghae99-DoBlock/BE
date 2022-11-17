package com.sparta.doblock.reaction.service;

import com.sparta.doblock.events.entity.BadgeEvents;
import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.feed.repository.FeedRepository;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.reaction.dto.request.ReactionRequestDto;
import com.sparta.doblock.reaction.entity.Reaction;
import com.sparta.doblock.reaction.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final FeedRepository feedRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public ResponseEntity<?> addReaction(Long feedId, ReactionRequestDto reactionRequestDto, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        Feed feed = feedRepository.findById(feedId).orElseThrow(
                () -> new NullPointerException("해당 피드가 없습니다")
        );

        if (! reactionRepository.existsByFeedAndMember(feed, memberDetails.getMember())) {
            Reaction reaction = Reaction.builder()
                    .feed(feed)
                    .member(memberDetails.getMember())
                    .reactionType(reactionRequestDto.getReactionType())
                    .build();

            reactionRepository.save(reaction);

            applicationEventPublisher.publishEvent(new BadgeEvents.CreateReactionBadgeEvent(memberDetails));

            return ResponseEntity.ok("성공적으로 리액션을 추가했습니다");

        } else {
            Reaction reaction = reactionRepository.findByFeedAndMember(feed, memberDetails.getMember()).orElseThrow(
                    NullPointerException::new
            );

            boolean delete = false;

            if (Objects.isNull(reactionRequestDto.getReactionType())) {
                delete = true;

            } else if (reactionRequestDto.getReactionType().equals(reaction.getReactionType())) {
                delete = true;
            }

            if (! delete) {
                reaction.update(reactionRequestDto.getReactionType());
                return ResponseEntity.ok("성공적으로 리액션을 수정하였습니다");

            } else {
                reactionRepository.delete(reaction);
                return ResponseEntity.ok("성공적으로 리액션을 삭제하였습니다");
            }
        }
    }
}
