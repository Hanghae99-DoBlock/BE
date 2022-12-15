package com.sparta.doblock.comment.repository;

import com.sparta.doblock.comment.entity.Comment;
import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByFeedOrderByPostedAt(Feed feed);
    Long countAllByMember(Member member);
    Long countAllByFeed(Feed feed);
    void deleteAllByFeed(Feed feed);
}
