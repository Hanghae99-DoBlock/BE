package com.sparta.doblock.feed.dto.response;

import com.sparta.doblock.comment.dto.response.CommentResponseDto;
import com.sparta.doblock.reaction.dto.response.ReactionResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class FeedResponseDto {
    private Long feedId;
    private Long memberId;
    private String profileImageUrl;
    private String nickname;
//    private List<Long> todoIdList;
    // TODO: discuss this in the meeting
    private List<String> todoList;
    private String content;
    private List<String> feedImagesUrlList;
    private List<String> tagList;
    private List<ReactionResponseDto> reactionResponseDtoList;
    private List<CommentResponseDto> commentResponseDtoList;
    private LocalDateTime postedAt;
}
