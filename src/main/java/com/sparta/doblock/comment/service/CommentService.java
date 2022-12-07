package com.sparta.doblock.comment.service;

import com.sparta.doblock.comment.dto.request.CommentRequestDto;
import com.sparta.doblock.comment.dto.response.CommentResponseDto;
import com.sparta.doblock.comment.entity.Comment;
import com.sparta.doblock.comment.repository.CommentRepository;
import com.sparta.doblock.events.entity.BadgeEvents;
import com.sparta.doblock.exception.DoBlockExceptions;
import com.sparta.doblock.exception.ErrorCodes;
import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.feed.repository.FeedRepository;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final FeedRepository feedRepository;
    private final CommentRepository commentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public ResponseEntity<?> addComment(Long feedId, CommentRequestDto commentRequestDto, MemberDetailsImpl memberDetails) {

        Feed feed = feedRepository.findById(feedId).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_FEED)
        );

        Comment comment = Comment.builder()
                .member(memberDetails.getMember())
                .feed(feed)
                .commentContent(commentRequestDto.getCommentContent())
                .build();

        commentRepository.save(comment);

        applicationEventPublisher.publishEvent(new BadgeEvents.SocialActiveBadgeEvent(memberDetails));

        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .commentId(comment.getId())
                .memberId(comment.getMember().getId())
                .profileImage(comment.getMember().getProfileImage())
                .nickname(comment.getMember().getNickname())
                .commentContent(comment.getCommentContent())
                .postedAt(comment.getPostedAt())
                .build();

        return ResponseEntity.ok(commentResponseDto);
    }

    public ResponseEntity<?> getCommentList(Long feedId) {

        Feed feed = feedRepository.findById(feedId).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_FEED)
        );

        List<Comment> commentList = commentRepository.findAllByFeedOrderByPostedAt(feed);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

        for (Comment comment : commentList) {
            commentResponseDtoList.add(
                    CommentResponseDto.builder()
                            .commentId(comment.getId())
                            .memberId(comment.getMember().getId())
                            .profileImage(comment.getMember().getProfileImage())
                            .nickname(comment.getMember().getNickname())
                            .commentContent(comment.getCommentContent())
                            .postedAt(comment.getPostedAt())
                            .build()
            );
        }

        return ResponseEntity.ok(commentResponseDtoList);
    }

    @Transactional
    public ResponseEntity<?> editComment(Long feedId, Long commentId, CommentRequestDto commentRequestDto, MemberDetailsImpl memberDetails) {

        Feed feed = feedRepository.findById(feedId).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_FEED)
        );

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_COMMENT)
        );

        if (!comment.getFeed().getId().equals(feed.getId())) {
            throw new DoBlockExceptions(ErrorCodes.NOT_MATCHED_FEED_COMMENT);

        } else if (!comment.getMember().getId().equals(memberDetails.getMember().getId())) {
            throw new DoBlockExceptions(ErrorCodes.NOT_VALID_WRITER);
        }

        comment.update(commentRequestDto);

        return ResponseEntity.ok("댓글을 성공적으로 수정하였습니다.");
    }

    @Transactional
    public ResponseEntity<?> deleteComment(Long feedId, Long commentId, MemberDetailsImpl memberDetails) {

        Feed feed = feedRepository.findById(feedId).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_FEED)
        );

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_COMMENT)
        );

        if (!comment.getFeed().getId().equals(feed.getId())) {
            throw new DoBlockExceptions(ErrorCodes.NOT_MATCHED_FEED_COMMENT);

        } else if (!comment.getMember().getId().equals(memberDetails.getMember().getId())) {
            throw new DoBlockExceptions(ErrorCodes.NOT_VALID_WRITER);

        } else {
            commentRepository.delete(comment);
            return ResponseEntity.ok("댓글을 성공적으로 삭제하였습니다.");
        }
    }
}
