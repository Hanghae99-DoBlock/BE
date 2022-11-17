package com.sparta.doblock.events.repository;

import com.sparta.doblock.events.entity.BadgeType;
import com.sparta.doblock.events.entity.Badges;
import com.sparta.doblock.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgesRepository extends JpaRepository<Badges, Long> {

    boolean existsByBadgeTypeAndMember(BadgeType badgeType, Member member);
}
