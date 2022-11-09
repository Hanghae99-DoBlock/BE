package com.sparta.doblock.comment.repository;

import com.sparta.doblock.comment.entity.Comment;
import com.sparta.doblock.feed.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByFeed(Feed feed);
}
