package com.sparta.doblock.profile.service;

import com.sparta.doblock.events.entity.BadgeType;
import com.sparta.doblock.events.entity.Badges;
import com.sparta.doblock.events.repository.BadgesRepository;
import com.sparta.doblock.exception.DoBlockExceptions;
import com.sparta.doblock.exception.ErrorCodes;
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

import java.util.*;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final MemberRepository memberRepository;
    private final BadgesRepository badgesRepository;

    public ResponseEntity<?> getBadgeList(Long memberId, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new DoBlockExceptions(ErrorCodes.NOT_LOGIN_MEMBER);
        }

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_MEMBER)
        );

        Badges selectedBadge = badgesRepository.findByMemberAndSelectedBadge(member, true).orElse(null);

        List<Badges> badgesList = badgesRepository.findAllByMember(member);

        HashSet<BadgeType> badgeTypes = new HashSet<>();
        List<BadgeResponseDto> badgeResponseDtoList = new ArrayList<>();

        for (Badges badges : badgesList) {
            badgeTypes.add(badges.getBadgeType());
        }

        for (BadgeType badgeType : BadgeType.values()) {
            badgeResponseDtoList.add(BadgeResponseDto.builder()
                    .badgeType(badgeType)
                    .badgeName(badgeType.getBadgeName())
                    .obtainedBadge(badgeTypes.contains(badgeType))
                    .build());
        }

        BadgeListResponseDto badgeListResponseDto = BadgeListResponseDto.builder()
                .badgeName(selectedBadge != null ? selectedBadge.getBadgeType().getBadgeName() : null)
                .badgeImage(selectedBadge != null ? selectedBadge.getBadgeType().getBadgeImage() : null)
                .badgeResponseDtoList(badgeResponseDtoList)
                .build();

        return ResponseEntity.ok(badgeListResponseDto);
    }

    public ResponseEntity<?> getBadges(Long memberId, String badgetype, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new DoBlockExceptions(ErrorCodes.NOT_LOGIN_MEMBER);
        }

        if (Objects.isNull(badgetype)) {
            throw new DoBlockExceptions(ErrorCodes.NOT_INPUT_BADGES);
        }

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_MEMBER)
        );

        Badges badges = badgesRepository.findByMemberAndBadgeType(member, BadgeType.valueOf(badgetype.toUpperCase())).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_OBTAINED_BADGES)
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
            throw new DoBlockExceptions(ErrorCodes.NOT_LOGIN_MEMBER);
        }

        if (Objects.isNull(badgesRequestDto.getBadgeType())) {
            throw new DoBlockExceptions(ErrorCodes.NOT_INPUT_BADGES);
        }

        Badges badges = badgesRepository.findByMemberAndBadgeType(memberDetails.getMember(), badgesRequestDto.getBadgeType()).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_OBTAINED_BADGES)
        );

        badgesRepository.findByMemberAndSelectedBadge(memberDetails.getMember(), true).ifPresent(Badges::selectBadge);
        badges.selectBadge();

        badgesRepository.save(badges);

        return ResponseEntity.ok("대표 뱃지가 설정 되었습니다");
    }
}
