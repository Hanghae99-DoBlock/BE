package com.sparta.doblock.comment.controller;

import com.sparta.doblock.comment.dto.request.CommentRequestDto;
import com.sparta.doblock.comment.service.CommentService;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feed")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{feedId}/comment")
    public ResponseEntity<?> addComment(@PathVariable Long feedId, @RequestBody CommentRequestDto commentRequestDto,
                                        @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return commentService.addComment(feedId, commentRequestDto, memberDetails);
    }

    @GetMapping("/{feedId}/comment")
    public ResponseEntity<?> getCommentList(@PathVariable Long feedId) {
        return commentService.getCommentList(feedId);
    }

    @PutMapping("/{feedId}/comment")
    public ResponseEntity<?> editComment(@PathVariable Long feedId, @RequestParam(name = "comment-id") Long commentId,
                                         @RequestBody CommentRequestDto commentRequestDto, @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return commentService.editComment(feedId, commentId, commentRequestDto, memberDetails);
    }

    @DeleteMapping("/{feedId}/comment")
    public ResponseEntity<?> deleteComment(@PathVariable Long feedId, @RequestParam(name = "comment-id") Long commentId,
                                         @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return commentService.deleteComment(feedId, commentId, memberDetails);
    }
}
