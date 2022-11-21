package com.sparta.doblock.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChatPayload {
    private Long chatMessageId;
    private String senderNickname;
    private String receiverNickname;
    private String message;
}
