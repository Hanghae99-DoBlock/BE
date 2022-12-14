package com.sparta.doblock.member.repository;

import com.sparta.doblock.member.entity.Authority;
import com.sparta.doblock.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNicknameAndAuthority(String nickname, Authority authority);
    Optional<Member> findBySocialId(String socialId);
}
