package com.sparta.doblock.profile.service;

import com.sparta.doblock.events.repository.BadgesRepository;
import com.sparta.doblock.exception.DoBlockExceptions;
import com.sparta.doblock.exception.ErrorCodes;
import com.sparta.doblock.feed.dto.response.FeedResponseDto;
import com.sparta.doblock.feed.repository.FeedRepository;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.member.repository.MemberRepository;
import com.sparta.doblock.profile.dto.request.EditPasswordRequestDto;
import com.sparta.doblock.profile.dto.request.EditProfileRequestDto;
import com.sparta.doblock.profile.dto.response.ProfileResponseDto;
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

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final FollowRepository followRepository;
    private final FeedRepository feedRepository;
    private final MemberRepository memberRepository;
    private final S3UploadService s3UploadService;
    private final PasswordEncoder passwordEncoder;
    private final TagRepository tagRepository;
    private final BadgesRepository badgesRepository;
    private final MemberTagMapperRepository memberTagMapperRepository;

    @Value("${profile.image}")
    private String defaultProfileImage;

    public ResponseEntity<?> getProfile(Long memberId, MemberDetailsImpl memberDetails) {

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_MEMBER)
        );

        ProfileResponseDto profileResponseDto = ProfileResponseDto.builder()
                .memberId(member.getId())
                .profileImage(member.getProfileImage())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .followOrNot(followRepository.existsByFromMemberAndToMember(memberDetails.getMember(), member))
                .countFeed(feedRepository.countAllByMember(member))
                .countFollower(followRepository.countAllByToMember(member))
                .countFollowing(followRepository.countAllByFromMember(member))
                .countBadge(badgesRepository.countAllByMember(member))
                .tagList(memberTagMapperRepository.findAllByMember(member).stream()
                        .map(tag -> tag.getTag().getTagContent())
                        .collect(Collectors.toList()))
                .badgeImageList(badgesRepository.findAllByMember(member).stream()
                        .map(badges -> badges.getBadgeType().getBadgeImage())
                        .collect(Collectors.toList()))
                .feedResponseDtoList(feedRepository.findTop3ByMemberOrderByPostedAtDesc(member).stream()
                        .map(feed -> FeedResponseDto.builder()
                                .feedId(feed.getId())
                                .feedTitle(feed.getFeedTitle())
                                .feedContent(feed.getFeedContent())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        return ResponseEntity.ok(profileResponseDto);
    }

    @Transactional
    public ResponseEntity<?> editProfile(EditProfileRequestDto editProfileRequestDto, MemberDetailsImpl memberDetails) throws IllegalAccessException, IOException {

        Member member = memberRepository.findByEmail(memberDetails.getMember().getEmail()).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_MEMBER)
        );

        if (editProfileRequestDto.checkNull()) {
            throw new DoBlockExceptions(ErrorCodes.NOT_INPUT_INFORMATION);
        }

        if (editProfileRequestDto.getProfileImage() != null) {

            if (!member.getProfileImage().equals(defaultProfileImage)) {
                s3UploadService.delete(member.getProfileImage());
            }

            String imageUrl = s3UploadService.uploadProfileImage(editProfileRequestDto.getProfileImage());
            member.editProfileImage(imageUrl);
        }

        if (editProfileRequestDto.getNickname() != null) {

            if (memberRepository.existsByNicknameAndAuthority(editProfileRequestDto.getNickname(), memberDetails.getMember().getAuthority())) {
                throw new DoBlockExceptions(ErrorCodes.DUPLICATED_NICKNAME);
            }

            member.editNickname(editProfileRequestDto.getNickname());
        }

        if (editProfileRequestDto.getTagList() != null) {

            if (editProfileRequestDto.getTagList().size() >= 4) {
                throw new DoBlockExceptions(ErrorCodes.EXCEED_MEMBER_TAG);
            }

            memberTagMapperRepository.deleteAllByMember(member);

            for (String tagContent : Objects.requireNonNull(editProfileRequestDto.getTagList())) {
                Tag tag = tagRepository.findByTagContent(tagContent).orElse(Tag.builder().tagContent(tagContent).build());

                tagRepository.save(tag);

                if (!memberTagMapperRepository.existsByMemberAndTag(member, tag)) {
                    MemberTagMapper memberTagMapper = MemberTagMapper.builder()
                            .tag(tag)
                            .member(member)
                            .build();

                    memberTagMapperRepository.save(memberTagMapper);
                }
            }
        }

        return ResponseEntity.ok("정보 변경 성공");
    }

    @Transactional
    public ResponseEntity<?> editPassword(EditPasswordRequestDto editPasswordRequestDto, MemberDetailsImpl memberDetails) throws IllegalAccessException {

        Member member = memberRepository.findByEmail(memberDetails.getMember().getEmail()).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_MEMBER)
        );

        if (editPasswordRequestDto.checkNull()) {
            throw new DoBlockExceptions(ErrorCodes.NOT_INPUT_INFORMATION);
        }

        if (editPasswordRequestDto.getNewPassword() != null) {

            if (!passwordEncoder.matches(editPasswordRequestDto.getCurrentPassword(), member.getPassword())) {
                throw new DoBlockExceptions(ErrorCodes.NOT_VALID_PASSWORD);
            }

            member.editPassword(passwordEncoder.encode(editPasswordRequestDto.getNewPassword()));
        }

        return ResponseEntity.ok("비밀번호 변경 성공");
    }
}
