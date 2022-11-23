package com.sparta.doblock.chat.service;

import com.sparta.doblock.chat.dto.ChatPayload;
import com.sparta.doblock.chat.entity.ChatMessage;
import com.sparta.doblock.chat.repository.ChatRepository;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    public ChatPayload sendChatMessage(ChatPayload chatPayload, MemberDetailsImpl memberDetails) throws Exception {
        if (Objects.isNull(memberDetails)) {
            throw new IllegalAccessException("로그인이 필요합니다");
        }
        Member sender = memberRepository.findByNickname(chatPayload.getSenderNickname()).orElseThrow(
                () -> new NullPointerException("전송자 아이디가 존재하지 않습니다")
        );
        if (! sender.isEqual(memberDetails.getMember())) {
            throw new IllegalAccessException("로그인된 회원과 발신자가 동일하지 않습니다");
        }

        Member receiver = memberRepository.findByNickname(chatPayload.getReceiverNickname()).orElseThrow(
                () -> new NullPointerException("수신자 아이디가 존재하지 않습니다")
        );

        ChatMessage chatMessage = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .message(chatPayload.getMessage())
                .build();

        chatRepository.save(chatMessage);

        simpMessagingTemplate.convertAndSendToUser(chatMessage.getReceiver().getNickname(), "/private", chatPayload);

        return chatPayload;
    }
}
