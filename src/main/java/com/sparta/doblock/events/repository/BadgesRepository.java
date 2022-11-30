package com.sparta.doblock.events.repository;

import com.sparta.doblock.events.entity.BadgeType;
import com.sparta.doblock.events.entity.Badges;
import com.sparta.doblock.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BadgesRepository extends JpaRepository<Badges, Long> {

    boolean existsByBadgeTypeAndMember(BadgeType badgeType, Member member);
    Long countAllByMember(Member member);
    List<Badges> findAllByMember(Member member);
    Optional<Badges> findByMemberAndSelectedBadge(Member member, boolean selectedBadge);
    Optional<Badges> findByMemberAndBadgeType(Member member, BadgeType badgeType);
}
