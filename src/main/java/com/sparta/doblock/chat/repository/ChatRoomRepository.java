package com.sparta.doblock.chat.repository;

import com.sparta.doblock.chat.entity.ChatRoom;
import com.sparta.doblock.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByHostAndGuest(Member host, Member guest);
    List<ChatRoom> findAllByHostOrGuest(Member host, Member guest);
}
