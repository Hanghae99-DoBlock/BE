package com.sparta.doblock.profile.entity;

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
public class Follow {

    @Id
    @Column(name = "follow_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // memberFollowing 이 memberFollowed 를 팔로우함

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_following_id")
    private Member memberFollowing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_followed_id")
    private Member memberFollowed;
}
