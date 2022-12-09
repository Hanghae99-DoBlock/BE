package com.sparta.doblock.reaction.service;

import com.sparta.doblock.events.entity.BadgeEvents;
import com.sparta.doblock.exception.DoBlockExceptions;
import com.sparta.doblock.exception.ErrorCodes;
import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.feed.repository.FeedRepository;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.reaction.dto.request.ReactionRequestDto;
import com.sparta.doblock.reaction.dto.response.ReactionResponseDto;
import com.sparta.doblock.reaction.entity.Reaction;
import com.sparta.doblock.reaction.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final FeedRepository feedRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public ResponseEntity<?> addReaction(Long feedId, ReactionRequestDto reactionRequestDto, MemberDetailsImpl memberDetails) {

        Feed feed = feedRepository.findById(feedId).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_FEED)
        );

        Reaction reaction = Reaction.builder()
                .feed(feed)
                .member(memberDetails.getMember())
                .reactionType(reactionRequestDto.getReactionType())
                .build();

        reactionRepository.save(reaction);

        applicationEventPublisher.publishEvent(new BadgeEvents.SocialActiveBadgeEvent(memberDetails));

        return ResponseEntity.ok("성공적으로 리액션을 추가했습니다");
    }

    @Transactional
    public ResponseEntity<?> editReaction(Long feedId, ReactionRequestDto reactionRequestDto, MemberDetailsImpl memberDetails) {

        Feed feed = feedRepository.findById(feedId).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_FEED)
        );

        Reaction reaction = reactionRepository.findByFeedAndMember(feed, memberDetails.getMember()).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_REACTION)
        );

        if (!reaction.getMember().getId().equals(memberDetails.getMember().getId())){
            throw new DoBlockExceptions(ErrorCodes.NOT_VALID_WRITER);
        }

        reaction.update(reactionRequestDto.getReactionType());

        return ResponseEntity.ok("성공적으로 리액션을 변경했습니다.");
    }

    @Transactional
    public ResponseEntity<?> deleteReaction(Long feedId, MemberDetailsImpl memberDetails) {

        Feed feed = feedRepository.findById(feedId).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_FEED)
        );

        Reaction reaction = reactionRepository.findByFeedAndMember(feed, memberDetails.getMember()).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_REACTION)
        );

        if (!reaction.getMember().getId().equals(memberDetails.getMember().getId())){
            throw new DoBlockExceptions(ErrorCodes.NOT_VALID_WRITER);
        }

        reactionRepository.delete(reaction);

        return ResponseEntity.ok("리액션을 성공적으로 삭제했습니다.");
    }

    public ResponseEntity<?> getRecentReaction(Long feedId) {

        Feed feed = feedRepository.findById(feedId).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_FEED)
        );

        List<Reaction> reactionList = reactionRepository.findTop2ByFeedOrderByModifiedAtDesc(feed);
        List<ReactionResponseDto> reactionResponseDtoList = new ArrayList<>();

        for (Reaction reaction : reactionList) {
            reactionResponseDtoList.add(
                    ReactionResponseDto.builder()
                            .reactionType(reaction.getReactionType())
                            .build()
            );
        }

        return ResponseEntity.ok(reactionResponseDtoList);
    }

    public ResponseEntity<?> getReactionList(Long feedId) {

        Feed feed = feedRepository.findById(feedId).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_FEED)
        );

        List<Reaction> reactionList = reactionRepository.findAllByFeedOrderByModifiedAtDesc(feed);
        List<ReactionResponseDto> reactionResponseDtoList = new ArrayList<>();

        for (Reaction reaction : reactionList) {
            reactionResponseDtoList.add(
                    ReactionResponseDto.builder()
                            .memberId(reaction.getMember().getId())
                            .profileImage(reaction.getMember().getProfileImage())
                            .nickname(reaction.getMember().getNickname())
                            .email(reaction.getMember().getEmail())
                            .reactionType(reaction.getReactionType())
                            .build()
            );
        }

        return ResponseEntity.ok(reactionResponseDtoList);
    }
}
