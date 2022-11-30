package com.sparta.doblock.profile.service;

import com.sparta.doblock.events.entity.BadgeEvents;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.member.repository.MemberRepository;
import com.sparta.doblock.profile.dto.response.FollowResponseDto;
import com.sparta.doblock.profile.entity.Follow;
import com.sparta.doblock.profile.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public ResponseEntity<?> follow(Long memberId, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        Member toMember = memberRepository.findById(memberId).orElseThrow(
                () -> new RuntimeException("사용자를 찾을 수 없습니다.")
        );

        if (toMember.getId().equals(memberDetails.getMember().getId())) {
            throw new RuntimeException("본인을 팔로우 할 수 없습니다.");
        }

        Optional<Follow> followingMember = followRepository.findByFromMemberAndToMember(memberDetails.getMember(), toMember);

        if (followingMember.isEmpty()) {
            Follow follow = Follow.builder()
                    .fromMember(memberDetails.getMember())
                    .toMember(toMember)
                    .build();

            followRepository.save(follow);

            applicationEventPublisher.publishEvent(new BadgeEvents.FollowToMemberBadgeEvent(memberDetails));

            return ResponseEntity.ok("팔로우 완료");

        } else {
            followRepository.deleteByFromMemberAndToMember(memberDetails.getMember(), toMember);

            return ResponseEntity.ok("팔로우 취소");
        }
    }

    public ResponseEntity<?> getFollowingList(Long memberId, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        Member fromMember = memberRepository.findById(memberId).orElseThrow(
                () -> new RuntimeException("사용자를 찾을 수 없습니다.")
        );

        List<Follow> followingList = followRepository.findAllByFromMember(fromMember);
        List<FollowResponseDto> followResponseDtoList = new ArrayList<>();

        for (Follow following : followingList) {
            followResponseDtoList.add(
                    FollowResponseDto.builder()
                            .memberId(following.getToMember().getId())
                            .profileImage(following.getToMember().getProfileImage())
                            .nickname(following.getToMember().getNickname())
                            .email(following.getToMember().getEmail())
                            .followOrNot(followRepository.existsByFromMemberAndToMember(memberDetails.getMember(), following.getToMember()))
                            .build()
            );
        }

        return ResponseEntity.ok(followResponseDtoList);
    }

    public ResponseEntity<?> getFollowerList(Long memberId, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        Member toMember = memberRepository.findById(memberId).orElseThrow(
                () -> new RuntimeException("사용자를 찾을 수 없습니다.")
        );

        List<Follow> followerList = followRepository.findAllByToMember(toMember);
        List<FollowResponseDto> followResponseDtoList = new ArrayList<>();

        for (Follow follower : followerList) {
            followResponseDtoList.add(
                    FollowResponseDto.builder()
                            .memberId(follower.getFromMember().getId())
                            .profileImage(follower.getFromMember().getProfileImage())
                            .nickname(follower.getFromMember().getNickname())
                            .email(follower.getFromMember().getEmail())
                            .followOrNot(followRepository.existsByFromMemberAndToMember(memberDetails.getMember(), follower.getFromMember()))
                            .build()
            );
        }

        return ResponseEntity.ok(followResponseDtoList);
    }
}
