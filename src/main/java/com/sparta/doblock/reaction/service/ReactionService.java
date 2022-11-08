package com.sparta.doblock.reaction.service;

import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.feed.repository.FeedRepository;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.reaction.dto.request.ReactionRequestDto;
import com.sparta.doblock.reaction.entity.Reaction;
import com.sparta.doblock.reaction.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final FeedRepository feedRepository;

    public ResponseEntity<?> addReaction(Long feedId, ReactionRequestDto reactionRequestDto, MemberDetailsImpl memberDetails) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(
                () -> new NullPointerException("해당 피드가 없습니다")
        );

        if (! reactionRepository.existsByFeedAndMember(feed, memberDetails.getMember())) {
            // add reaction
            Reaction reaction = Reaction.builder()
                    .feed(feed)
                    .member(memberDetails.getMember())
                    .reactionType(reactionRequestDto.getReactionType())
                    .build();

            reactionRepository.save(reaction);

            return ResponseEntity.ok("성공적으로 리액션을 추가했습니다");
        } else {
            Reaction reaction = reactionRepository.findByFeedAndMember(feed, memberDetails.getMember()).orElseThrow(
                    () -> new NullPointerException()
            );
            reactionRepository.delete(reaction);

            return ResponseEntity.ok("성공적으로 리액션을 삭제하였습니다");
        }
    }
}
