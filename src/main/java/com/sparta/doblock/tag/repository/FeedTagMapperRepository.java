package com.sparta.doblock.tag.repository;

import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.tag.entity.Tag;
import com.sparta.doblock.tag.mapper.FeedTagMapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedTagMapperRepository extends JpaRepository<FeedTagMapper, Long> {

    List<FeedTagMapper> findAllByTag(Tag tag);
    List<FeedTagMapper> findAllByFeed(Feed feed);
    List<FeedTagMapper> findTop5ByTagOrderByIdDesc(Tag tag);
    void deleteAllByFeed(Feed feed);
    boolean existsByFeedAndTag(Feed feed, Tag tag);
}
