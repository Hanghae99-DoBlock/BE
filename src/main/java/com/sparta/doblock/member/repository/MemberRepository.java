package com.sparta.doblock.member.repository;

import com.sparta.doblock.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findBySocialId(String socialId);
    
    // O(log n + |tags|) with B-Tree implementation
    @Query("SELECT m FROM Member m WHERE m.nickname LIKE %:nickname%")
    List<Member> searchByMemberLike(@Param("nickname") String nickname);
}
