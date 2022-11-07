package com.sparta.doblock.profile.service;

import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.member.repository.MemberRepository;
import com.sparta.doblock.profile.dto.response.FollowResponseDto;
import com.sparta.doblock.profile.entity.Follow;
import com.sparta.doblock.profile.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ResponseEntity<?> follow(Long memberId, Member member) {

        Member toMember = memberRepository.findById(memberId).orElseThrow(
                () -> new RuntimeException("사용자를 찾을 수 없습니다.")
        );

        Optional<Follow> followingMember = followRepository.findByFromMemberAndToMember(member, toMember);

        if (followingMember.isEmpty()){
            Follow follow = Follow.builder()
                    .fromMember(member)
                    .toMember(toMember)
                    .build();

            followRepository.save(follow);

            return new ResponseEntity<>("팔로우 완료", HttpStatus.OK);

        }else {
            followRepository.deleteByFromMemberAndToMember(member, toMember);

            return new ResponseEntity<>("팔로우 취소", HttpStatus.OK);
        }
    }

    public ResponseEntity<?> getFollowingList(Long memberId, Member member) {

        Member fromMember = memberRepository.findById(memberId).orElseThrow(
                () -> new RuntimeException("사용자를 찾을 수 없습니다.")
        );

        List<Follow> followingList = followRepository.findAllByFromMember(fromMember);
        List<FollowResponseDto> followResponseDtoList = new ArrayList<>();

        for (Follow following : followingList){
            followResponseDtoList.add(
                    FollowResponseDto.builder()
                            .memberId(following.getToMember().getId())
                            .profileImage(following.getToMember().getProfileImage())
                            .nickname(following.getToMember().getNickname())
                            .build()
            );
        }

        return new ResponseEntity<>(followResponseDtoList, HttpStatus.OK);
    }

    public ResponseEntity<?> getFollowerList(Long memberId, Member member) {

        Member toMember = memberRepository.findById(memberId).orElseThrow(
                () -> new RuntimeException("사용자를 찾을 수 없습니다.")
        );

        List<Follow> followerList = followRepository.findAllByToMember(toMember);
        List<FollowResponseDto> followResponseDtoList = new ArrayList<>();

        for (Follow follower : followerList){
            followResponseDtoList.add(
                    FollowResponseDto.builder()
                            .memberId(follower.getFromMember().getId())
                            .profileImage(follower.getFromMember().getProfileImage())
                            .nickname(follower.getFromMember().getNickname())
                            .build()
            );
        }

        return new ResponseEntity<>(followResponseDtoList, HttpStatus.OK);
    }
}
