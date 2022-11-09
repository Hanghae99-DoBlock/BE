package com.sparta.doblock.tag.repository;

import com.sparta.doblock.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    public boolean existsByContent(String content);
    Optional<Tag> findByContent(String content);

}
