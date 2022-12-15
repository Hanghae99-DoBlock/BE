package com.sparta.doblock.feed.repository;

import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.profile.entity.Follow;

import java.util.List;

public interface FeedRepositoryCustom {

    List<Feed> searchAllByFollow(Long lastId, Member fromMember, List<Follow> followingList);
    List<Feed> searchAllByMember(Long lastId, Member member);
}
