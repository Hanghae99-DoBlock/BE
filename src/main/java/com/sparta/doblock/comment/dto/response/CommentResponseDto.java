package com.sparta.doblock.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CommentResponseDto {

    private Long commentId;
    private Long memberId;
    private String profileImage;
    private String nickname;
    private String commentContent;
    private LocalDateTime postedAt;
}
