package com.sparta.doblock.reaction.service;

import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.feed.repository.FeedRepository;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.reaction.dto.request.ReactionRequestDto;
import com.sparta.doblock.reaction.entity.Reaction;
import com.sparta.doblock.reaction.entity.ReactionType;
import com.sparta.doblock.reaction.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final FeedRepository feedRepository;

    public ResponseEntity<?> addReaction(Long feedId, ReactionRequestDto reactionRequestDto, MemberDetailsImpl memberDetails) {
        if (Objects.isNull(memberDetails)) {
            return new ResponseEntity<>("로그인이 필요합니다", HttpStatus.UNAUTHORIZED);
        }

        Feed feed = feedRepository.findById(feedId).orElseThrow(
                () -> new NullPointerException("해당 피드가 없습니다")
        );

        reactionRequestDto.capitalize();

        if (! reactionRepository.existsByFeedAndMember(feed, memberDetails.getMember())) {
            // add reaction
            Reaction reaction = Reaction.builder()
                    .feed(feed)
                    .member(memberDetails.getMember())
                    .reactionType(ReactionType.valueOf(reactionRequestDto.getType()))
                    .build();

            reactionRepository.save(reaction);

            return ResponseEntity.ok("성공적으로 리액션을 추가했습니다");
        } else {
            Reaction reaction = reactionRepository.findByFeedAndMember(feed, memberDetails.getMember()).orElseThrow(
                    () -> new NullPointerException()
            );
            boolean delete = false;
            if (Objects.isNull(reactionRequestDto.getType())) {
                delete = true;
            } else if (Objects.isNull(ReactionType.valueOf(reactionRequestDto.getType()))) {
                delete = true;
            } else if (ReactionType.valueOf(reactionRequestDto.getType()).equals(reaction.getReactionType())) {
                delete = true;
            }

            if (! delete) {
                // change reaction type
                reaction.update(ReactionType.valueOf(reactionRequestDto.getType()));
                return ResponseEntity.ok("성공적으로 리액션을 수정하였습니다");
            } else {
                reactionRepository.delete(reaction);
                return ResponseEntity.ok("성공적으로 리액션을 삭제하였습니다");
            }
        }
    }
}
