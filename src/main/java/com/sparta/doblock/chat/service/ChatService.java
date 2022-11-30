package com.sparta.doblock.chat.service;

import com.sparta.doblock.chat.dto.request.ChatMessageRequestDto;
import com.sparta.doblock.chat.dto.response.ChatMessageResponseDto;
import com.sparta.doblock.chat.dto.response.ChatRoomResponseDto;
import com.sparta.doblock.chat.entity.ChatMessage;
import com.sparta.doblock.chat.entity.ChatRoom;
import com.sparta.doblock.chat.repository.ChatMessageRepository;
import com.sparta.doblock.chat.repository.ChatRoomRepository;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ResponseEntity<?> createChatRoom(Long memberId, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        Member guest = memberRepository.findById(memberId).orElseThrow(
                () -> new RuntimeException("사용자를 찾을 수 없습니다.")
        );

        if (guest.getId().equals(memberDetails.getMember().getId())) {
            throw new RuntimeException("본인에게 메세지를 보낼 수 없습니다.");
        }

        ChatRoom chatRoom = chatRoomRepository.findByHostAndGuest(memberDetails.getMember(), guest).orElse(
                chatRoomRepository.findByHostAndGuest(guest, memberDetails.getMember()).orElse(
                        ChatRoom.builder()
                                .host(memberDetails.getMember())
                                .guest(guest)
                                .build()
                )
        );

        chatRoomRepository.save(chatRoom);

        HttpHeaders redirectUri = new HttpHeaders();
        redirectUri.setLocation(URI.create("http://localhost:8080/api/chat/rooms/" + chatRoom.getId()));

        return ResponseEntity.ok().headers(redirectUri).body("채팅방으로 이동합니다.");
    }

    public ResponseEntity<?> getChatRooms(MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByHostOrGuest(memberDetails.getMember(), memberDetails.getMember());
        List<ChatRoomResponseDto> chatRoomResponseDtoList = new ArrayList<>();

        for (ChatRoom chatRoom : chatRoomList) {

            ChatMessage latestChatMessage = chatMessageRepository.findByChatRoomOrderByPostedAtDesc(chatRoom).orElse(null);

            chatRoomResponseDtoList.add(ChatRoomResponseDto.builder()
                    .chatRoomId(chatRoom.getId())
                    .hostId(chatRoom.getHost().getId())
                    .hostNickname(chatRoom.getHost().getNickname())
                    .hostProfileImage(chatRoom.getHost().getProfileImage())
                    .guestId(chatRoom.getGuest().getId())
                    .guestNickname(chatRoom.getGuest().getNickname())
                    .guestProfileImage(chatRoom.getGuest().getProfileImage())
                    .latestChatMessage(latestChatMessage != null ? latestChatMessage.getMessageContent() : null)
                    .lastPostedAt(latestChatMessage != null ? latestChatMessage.getPostedAt() : null)
                    .build()
            );
        }

        chatRoomResponseDtoList.sort((o1, o2) -> o2.getLastPostedAt().compareTo(o1.getLastPostedAt()));

        return ResponseEntity.ok(chatRoomResponseDtoList);
    }

    public ResponseEntity<?> getChatMessages(Long roomId, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new RuntimeException("채팅방이 존재하지 않습니다.")
        );

        if (!chatRoom.getHost().getId().equals(memberDetails.getMember().getId()) && !chatRoom.getGuest().getId().equals(memberDetails.getMember().getId())) {
            throw new RuntimeException("채팅방을 이용할 수 없는 사용자입니다.");
        }

        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByChatRoomOrderByPostedAtAsc(chatRoom);
        List<ChatMessageResponseDto> chatMessageResponseDtoList = new ArrayList<>();

        for (ChatMessage chatMessage : chatMessageList) {
            chatMessageResponseDtoList.add(ChatMessageResponseDto.builder()
                    .senderId(chatMessage.getSender().getId())
                    .profileImage(chatMessage.getSender().getProfileImage())
                    .nickname(chatMessage.getSender().getNickname())
                    .messageContent(chatMessage.getMessageContent())
                    .postedAt(chatMessage.getPostedAt())
                    .build()
            );
        }

        return ResponseEntity.ok(chatMessageResponseDtoList);
    }

    @Transactional
    public ChatMessageResponseDto sendMessage(Long roomId, ChatMessageRequestDto chatMessageRequestDto) {

        Member sender = memberRepository.findById(chatMessageRequestDto.getSenderId()).orElseThrow(
                () -> new RuntimeException("사용자를 찾을 수 없습니다.")
        );

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new RuntimeException("채팅방이 존재하지 않습니다.")
        );

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .messageContent(chatMessageRequestDto.getMessageContent())
                .build();

        chatMessageRepository.save(chatMessage);

        return ChatMessageResponseDto.builder()
                .chatMessageId(chatMessage.getId())
                .senderId(chatMessage.getSender().getId())
                .profileImage(chatMessage.getSender().getProfileImage())
                .nickname(chatMessage.getSender().getNickname())
                .messageContent(chatMessage.getMessageContent())
                .postedAt(chatMessage.getPostedAt())
                .build();
    }
}
