package com.sparta.doblock.feed.repository;

import com.sparta.doblock.feed.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long> {
}
