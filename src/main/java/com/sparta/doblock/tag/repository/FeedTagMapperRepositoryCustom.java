package com.sparta.doblock.tag.repository;

import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.tag.mapper.MemberTagMapper;

import java.util.List;

public interface FeedTagMapperRepositoryCustom {

    List<Feed> searchAllByFeedTagLike(Long lastId, String keyword);
    List<Feed> searchAllByMemberTagLike(Long lastId, List<MemberTagMapper> memberTagMapperList);
}
