package com.sparta.doblock.reaction.repository;

import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.reaction.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    Boolean existsByFeedAndMember(Feed feed, Member member);
    Optional<Reaction> findByFeedAndMember(Feed feed, Member member);
    List<Reaction> findAllByFeedOrderByPostedAtDesc(Feed feed);
    List<Reaction> findTop2ByFeedOrderByPostedAtDesc(Feed feed);
    Long countAllByMember(Member member);
    Long countAllByFeed(Feed feed);
}
