package com.sparta.doblock.profile.service;

import com.sparta.doblock.exception.CustomExceptions;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.member.repository.MemberRepository;
import com.sparta.doblock.profile.dto.request.EditProfileRequestDto;
import com.sparta.doblock.profile.dto.response.FollowResponseDto;
import com.sparta.doblock.profile.entity.Follow;
import com.sparta.doblock.profile.repository.FollowRepository;
import com.sparta.doblock.tag.entity.Tag;
import com.sparta.doblock.tag.mapper.MemberTagMapper;
import com.sparta.doblock.tag.repository.MemberTagMapperRepository;
import com.sparta.doblock.tag.repository.TagRepository;
import com.sparta.doblock.util.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final S3UploadService s3UploadService;
    private final PasswordEncoder passwordEncoder;
    private final TagRepository tagRepository;
    private final MemberTagMapperRepository memberTagMapperRepository;

    @Value("${profile.image}")
    private String defaultProfileImage;

    @Transactional
    public ResponseEntity<?> editProfile(EditProfileRequestDto editProfileRequestDto, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        if (memberRepository.existsByNickname(editProfileRequestDto.getNickname())){
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        Member member = memberRepository.findByEmail(memberDetails.getMember().getEmail()).orElseThrow(
                () -> new RuntimeException("사용자를 찾을 수 없습니다.")
        );

        if(editProfileRequestDto.getProfileImage() != null){

            if(!member.getProfileImage().equals(defaultProfileImage)){
                s3UploadService.delete(member.getProfileImage());
            }

            String imageUrl = s3UploadService.uploadImage(editProfileRequestDto.getProfileImage());
            member.editProfileImage(imageUrl);
        }

        if(editProfileRequestDto.getNewPassword() != null){

            if(passwordEncoder.matches(editProfileRequestDto.getCurrentPassword(), member.getPassword())){
                throw new CustomExceptions.NotMatchedPasswordException();
            }

            member.editPassword(passwordEncoder.encode(editProfileRequestDto.getNewPassword()));
        }

        member.editNickname(editProfileRequestDto.getNickname());

        memberTagMapperRepository.deleteAllByMember(member);

        if (editProfileRequestDto.getTagList().size() >= 4){
            throw new RuntimeException("관심사 태그는 유저 당 3개만 가능합니다.");
        }

        for (String tagContent : Objects.requireNonNull(editProfileRequestDto.getTagList())) {
            Tag tag = tagRepository.findByTagContent(tagContent).orElse(Tag.builder().tagContent(tagContent).build());

            tagRepository.save(tag);

            MemberTagMapper memberTagMapper = MemberTagMapper.builder()
                    .tag(tag)
                    .member(member)
                    .build();

            memberTagMapperRepository.save(memberTagMapper);
        }

        return ResponseEntity.ok("정보 변경 성공");
    }


    @Transactional
    public ResponseEntity<?> follow(String nickname, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        Member toMember = memberRepository.findByNickname(nickname).orElseThrow(
                () -> new RuntimeException("사용자를 찾을 수 없습니다.")
        );

        if (toMember.getNickname().equals(memberDetails.getMember().getNickname())){
            throw new RuntimeException("본인을 팔로우 할 수 없습니다.");
        }

        Optional<Follow> followingMember = followRepository.findByFromMemberAndToMember(memberDetails.getMember(), toMember);

        if (followingMember.isEmpty()){
            Follow follow = Follow.builder()
                    .fromMember(memberDetails.getMember())
                    .toMember(toMember)
                    .build();

            followRepository.save(follow);

            return ResponseEntity.ok("팔로우 완료");

        }else {
            followRepository.deleteByFromMemberAndToMember(memberDetails.getMember(), toMember);

            return ResponseEntity.ok("팔로우 취소");
        }
    }

    public ResponseEntity<?> getFollowingList(String nickname, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

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

        return ResponseEntity.ok(followResponseDtoList);
    }

    public ResponseEntity<?> getFollowerList(String nickname, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

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

        return ResponseEntity.ok(followResponseDtoList);
    }
}
