package com.sparta.doblock.chat.repository;

import com.sparta.doblock.chat.entity.ChatMessage;
import com.sparta.doblock.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Optional<ChatMessage> findByChatRoomOrderByPostedAtDesc(ChatRoom chatRoom);
    List<ChatMessage> findAllByChatRoomOrderByPostedAtAsc(ChatRoom chatRoom);
}
