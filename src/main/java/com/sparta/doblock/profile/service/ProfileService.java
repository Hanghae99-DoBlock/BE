package com.sparta.doblock.profile.service;

import com.sparta.doblock.exception.CustomExceptions;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.member.repository.MemberRepository;
import com.sparta.doblock.profile.controller.EditProfileRequestDto;
import com.sparta.doblock.profile.dto.response.FollowResponseDto;
import com.sparta.doblock.profile.entity.Follow;
import com.sparta.doblock.profile.repository.FollowRepository;
import com.sparta.doblock.util.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final S3UploadService s3UploadService;
    private final PasswordEncoder passwordEncoder;

    @Value("${profile.image}")
    private String defaultProfileImage;

    @Transactional
    public ResponseEntity<?> editProfile(EditProfileRequestDto editProfileRequestDto, Member member) {

        if(passwordEncoder.matches(editProfileRequestDto.getCurrentPassword(), member.getPassword())){
            throw new CustomExceptions.NotMatchedPasswordException();
        }

        if(editProfileRequestDto.getProfileImage() != null){

            if(!member.getProfileImage().equals(defaultProfileImage)){
                s3UploadService.delete(member.getProfileImage());
            }

            String imageUrl = s3UploadService.uploadImage(editProfileRequestDto.getProfileImage());
            member.editProfileImage(imageUrl);
        }

        if(editProfileRequestDto.getNewPassword() != null){
            member.editPassword(passwordEncoder.encode(editProfileRequestDto.getNewPassword()));
        }

        member.editNickname(editProfileRequestDto.getNickname());

        memberRepository.save(member);

        return new ResponseEntity<>("정보 변경 성공", HttpStatus.OK);
    }


    @Transactional
    public ResponseEntity<?> follow(String nickname, Member member) {

        Member toMember = memberRepository.findByNickname(nickname).orElseThrow(
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

    public ResponseEntity<?> getFollowingList(String nickname, Member member) {

        Member fromMember = memberRepository.findByNickname(nickname).orElseThrow(
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

    public ResponseEntity<?> getFollowerList(String nickname, Member member) {

        Member toMember = memberRepository.findByNickname(nickname).orElseThrow(
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
