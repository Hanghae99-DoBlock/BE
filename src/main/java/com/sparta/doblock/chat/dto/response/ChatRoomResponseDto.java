package com.sparta.doblock.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomResponseDto {

    private Long chatRoomId;
    private Long hostId;
    private String hostNickname;
    private String hostProfileImage;
    private Long guestId;
    private String guestNickname;
    private String guestProfileImage;
    private String latestChatMessage;
    private LocalDateTime lastPostedAt;
}
