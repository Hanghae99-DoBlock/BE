package com.sparta.doblock.profile.service;

import com.sparta.doblock.events.entity.Badges;
import com.sparta.doblock.events.repository.BadgesRepository;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.member.repository.MemberRepository;
import com.sparta.doblock.profile.dto.request.BadgesRequestDto;
import com.sparta.doblock.profile.dto.response.BadgeListResponseDto;
import com.sparta.doblock.profile.dto.response.BadgeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final MemberRepository memberRepository;
    private final BadgesRepository badgesRepository;

    public ResponseEntity<?> getBadgeList(Long memberId, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new RuntimeException("사용자를 찾을 수 없습니다.")
        );

        Badges selectedBadge = badgesRepository.findByMemberAndSelectedBadge(member, true).orElse(null);

        List<Badges> badgesList = badgesRepository.findAllByMember(member);

        BadgeListResponseDto badgeListResponseDto = BadgeListResponseDto.builder()
                .badgeName(selectedBadge != null ? selectedBadge.getBadgeType().getBadgeName() : null)
                .badgeImage(selectedBadge != null ? selectedBadge.getBadgeType().getBadgeImage() : null)
                .build();

        badgeListResponseDto.editBadgeListDto(badgesList);

        return ResponseEntity.ok(badgeListResponseDto);
    }

    public ResponseEntity<?> getBadges(Long memberId, BadgesRequestDto badgesRequestDto, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        if (Objects.isNull(badgesRequestDto.getBadgeType())) {
            throw new NullPointerException("뱃지를 선택해주세요.");
        }

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new RuntimeException("사용자를 찾을 수 없습니다.")
        );

        Badges badges = badgesRepository.findByMemberAndBadgeType(member, badgesRequestDto.getBadgeType()).orElseThrow(
                () -> new RuntimeException("사용자가 획득한 뱃지가 아닙니다.")
        );

        BadgeResponseDto badgeResponseDto = BadgeResponseDto.builder()
                .badgeName(badges.getBadgeType().getBadgeName())
                .badgeImage(badges.getBadgeType().getBadgeImage())
                .badgeDetail(badges.getBadgeType().getBadgeDetail())
                .build();

        return ResponseEntity.ok(badgeResponseDto);
    }

    @Transactional
    public ResponseEntity<?> editBadges(BadgesRequestDto badgesRequestDto, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        if (Objects.isNull(badgesRequestDto.getBadgeType())) {
            throw new NullPointerException("뱃지를 선택해주세요.");
        }

        Badges badges = badgesRepository.findByMemberAndBadgeType(memberDetails.getMember(), badgesRequestDto.getBadgeType()).orElseThrow(
                () -> new RuntimeException("사용자가 획득한 뱃지가 아닙니다.")
        );

        badgesRepository.findByMemberAndSelectedBadge(memberDetails.getMember(), true).ifPresent(Badges::selectBadge);
        badges.selectBadge();

        badgesRepository.save(badges);

        return ResponseEntity.ok("대표 뱃지가 설정 되었습니다");
    }
}
