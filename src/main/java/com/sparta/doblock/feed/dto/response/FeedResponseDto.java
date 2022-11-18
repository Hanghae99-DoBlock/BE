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
    private boolean followOrNot;
    private List<String> todoList;
    private String feedTitle;
    private String feedContent;
    private List<String> feedImagesUrlList;
    private List<String> tagList;
    private String feedColor;
    private boolean eventFeed;
    private List<ReactionResponseDto> reactionResponseDtoList;
    private List<CommentResponseDto> commentResponseDtoList;
    private LocalDateTime postedAt;
}
