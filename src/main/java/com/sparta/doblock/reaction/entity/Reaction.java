package com.sparta.doblock.reaction.entity;

import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.util.TimeStamp;
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
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "member_id", "feed_id" }) })
public class Reaction extends TimeStamp {

    @Id
    @Column(name = "reaction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    ReactionType reactionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed;

    public void update(ReactionType reactionType) {
        this.reactionType = reactionType;
    }
}
