package com.sparta.doblock.chat.controller;

import com.sparta.doblock.chat.dto.request.ChatMessageRequestDto;
import com.sparta.doblock.chat.service.ChatService;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/api/chat/rooms")
    public ResponseEntity<?> createChatRoom(@RequestParam("memberId") Long memberId, @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        return chatService.createChatRoom(memberId, memberDetails);
    }

    @GetMapping("/api/chat/rooms")
    public ResponseEntity<?> getChatRooms(@AuthenticationPrincipal MemberDetailsImpl memberDetails){
        return chatService.getChatRooms(memberDetails);
    }

    @GetMapping("/api/chat/rooms/{roomId}")
    public ResponseEntity<?> getChatMessages(@PathVariable(name = "roomId") Long roomId, @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        return chatService.getChatMessages(roomId, memberDetails);
    }

    @MessageMapping("/chat/rooms/{roomId}")
    public void sendMessage(@PathVariable(name = "roomId") Long roomId, @Payload ChatMessageRequestDto chatMessageRequestDto){
        simpMessagingTemplate.convertAndSend("/chat/rooms" + roomId, chatService.sendMessage(roomId, chatMessageRequestDto));
    }
}
