package com.sparta.doblock.feed.repository;

import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long>, FeedRepositoryCustom{

    List<Feed> findTop3ByMemberOrderByPostedAtDesc(Member member);
    Long countAllByMember(Member member);
}
