package com.sparta.doblock.chat.entity;

import com.sparta.doblock.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_member_id")
    private Member host;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_member_id")
    private Member guest;
}
