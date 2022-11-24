package com.sparta.doblock.feed.repository;

import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.member.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    List<Feed> findAllByMember(Member member);
    List<Feed> findTop3ByMemberOrderByPostedAtDesc(Member member);
    Long countAllByMember(Member member);
    Slice<Feed> findByMember(Member member, Pageable pageable);
}
