package com.sparta.doblock.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ChatMessageResponseDto {

    private Long chatMessageId;
    private Long senderId;
    private String profileImage;
    private String nickname;
    private String messageContent;
    private LocalDateTime postedAt;
}
