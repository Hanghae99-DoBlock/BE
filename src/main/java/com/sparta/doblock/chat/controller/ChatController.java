package com.sparta.doblock.chat.controller;

import com.sparta.doblock.chat.dto.ChatPayload;
import com.sparta.doblock.chat.service.ChatService;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public ChatPayload receiveMessage(@Payload ChatPayload chatPayload) {
        return chatPayload;
    }

    @MessageMapping("/private-message")
    public ChatPayload sendMessage(@Payload ChatPayload chatPayload,
                                   @AuthenticationPrincipal MemberDetailsImpl memberDetails) throws Exception {
        return chatService.sendChatMessage(chatPayload, memberDetails);
    }
}
