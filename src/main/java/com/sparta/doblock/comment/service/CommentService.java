package com.sparta.doblock.comment.service;

import com.sparta.doblock.comment.dto.request.CommentRequestDto;
import com.sparta.doblock.comment.entity.Comment;
import com.sparta.doblock.comment.repository.CommentRepository;
import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.feed.repository.FeedRepository;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final FeedRepository feedRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public ResponseEntity<?> addComment(Long feedId, CommentRequestDto commentRequestDto, MemberDetailsImpl memberDetails) {

        Feed feed = feedRepository.findById(feedId).orElseThrow(
                () -> new NullPointerException("해당 피드가 존재하지 않습니다")
        );

        if (Objects.isNull(memberDetails)) {
            return new ResponseEntity<>("로그인이 필요합니다", HttpStatus.UNAUTHORIZED);
        }

        Comment comment = Comment.builder()
                .member(memberDetails.getMember())
                .feed(feed)
                .commentContent(commentRequestDto.getCommentContent())
                .build();

        commentRepository.save(comment);

        return ResponseEntity.ok("댓글을 성공적으로 생성하였습니다");
    }

    @Transactional
    public ResponseEntity<?> editComment(Long feedId, Long commentId, CommentRequestDto commentRequestDto, MemberDetailsImpl memberDetails) {

        Feed feed = feedRepository.findById(feedId).orElseThrow(
                () -> new NullPointerException("해당 피드가 존재하지 않습니다")
        );

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NullPointerException("해당 댓글이 존재하지 않습니다")
        );

        if (Objects.isNull(memberDetails)) {
            return new ResponseEntity<>("로그인이 필요합니다", HttpStatus.UNAUTHORIZED);

        } else if (!comment.getFeed().isEqual(feed)) {
            return new ResponseEntity<>("댓글과 포스트가 일치하지 않습니다", HttpStatus.BAD_REQUEST);

        } else if (!comment.getMember().isEqual(memberDetails.getMember())) {
            return new ResponseEntity<>("본인 댓글만 수정 가능합니다", HttpStatus.FORBIDDEN);
        }

        comment.update(commentRequestDto);

        return ResponseEntity.ok("댓글을 성공적으로 수정하였습니다");
    }

    @Transactional
    public ResponseEntity<?> deleteComment(Long feedId, Long commentId, MemberDetailsImpl memberDetails) {

        Feed feed = feedRepository.findById(feedId).orElseThrow(
                () -> new NullPointerException("해당 피드가 존재하지 않습니다")
        );

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NullPointerException("해당 댓글이 존재하지 않습니다")
        );

        if (Objects.isNull(memberDetails)) {
            return new ResponseEntity<>("로그인이 필요합니다", HttpStatus.UNAUTHORIZED);

        } else if (!comment.getFeed().isEqual(feed)) {
            return new ResponseEntity<>("댓글과 포스트가 일치하지 않습니다", HttpStatus.BAD_REQUEST);

        } else if (!comment.getMember().isEqual(memberDetails.getMember())) {
            return new ResponseEntity<>("댓글을 작성한 유저가 아닙니다", HttpStatus.FORBIDDEN);

        } else {
            commentRepository.delete(comment);
            return ResponseEntity.ok("댓글을 성공적으로 삭제하였습니다");
        }
    }
}
