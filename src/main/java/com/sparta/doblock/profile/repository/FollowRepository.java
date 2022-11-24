package com.sparta.doblock.profile.repository;

import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.profile.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFromMemberAndToMember(Member fromMember, Member toMember);
    boolean existsByFromMemberAndToMember(Member fromMember, Member toMember);
    void deleteByFromMemberAndToMember(Member fromMember, Member toMember);
    List<Follow> findAllByFromMember(Member fromMember);
    List<Follow> findAllByToMember(Member toMember);
    Long countAllByFromMember(Member fromMember);
    Long countAllByToMember(Member toMember);
}
