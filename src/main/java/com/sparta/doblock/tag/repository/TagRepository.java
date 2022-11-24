package com.sparta.doblock.tag.repository;

import com.sparta.doblock.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByTagContent(String tagContent);

    // O(log n + |tags|) with B-Tree implementation
    @Query("SELECT t FROM Tag t WHERE t.tagContent LIKE %:tagContent%")
    List<Tag> searchByTagLike(@Param("tagContent") String tagContent);
}
