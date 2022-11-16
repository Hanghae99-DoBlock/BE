package com.sparta.doblock.search.service;

import com.sparta.doblock.comment.dto.response.CommentResponseDto;
import com.sparta.doblock.comment.repository.CommentRepository;
import com.sparta.doblock.feed.dto.response.FeedResponseDto;
import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.feed.repository.FeedRepository;
import com.sparta.doblock.member.dto.response.MemberResponseDto;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.member.repository.MemberRepository;
import com.sparta.doblock.profile.entity.Follow;
import com.sparta.doblock.profile.repository.FollowRepository;
import com.sparta.doblock.reaction.dto.response.ReactionResponseDto;
import com.sparta.doblock.reaction.repository.ReactionRepository;
import com.sparta.doblock.tag.entity.Tag;
import com.sparta.doblock.tag.mapper.FeedTagMapper;
import com.sparta.doblock.tag.mapper.MemberTagMapper;
import com.sparta.doblock.tag.repository.FeedTagMapperRepository;
import com.sparta.doblock.tag.repository.MemberTagMapperRepository;
import com.sparta.doblock.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final FeedRepository feedRepository;
    private final ReactionRepository reactionRepository;
    private final CommentRepository commentRepository;
    private final TagRepository tagRepository;
    private final FeedTagMapperRepository feedTagMapperRepository;
    private final MemberTagMapperRepository memberTagMapperRepository;

    @Transactional
    public ResponseEntity<?> search(String keyword, String category) {

        if (category.equals("feed")) {
            // feed search
            Tag tag = tagRepository.findByTagContent(keyword).orElseThrow(
                    () -> new NullPointerException("해당 검색어에 맞는 피드가 없습니다.")
            );

            List<FeedTagMapper> feedTagMapperList = feedTagMapperRepository.findAllByTag(tag);
            List<FeedResponseDto> feedResponseDtoList = new ArrayList<>();

            for (FeedTagMapper feedTagMapper : feedTagMapperList) {
                Feed feed = feedTagMapper.getFeed();
                addFeed(feedResponseDtoList, feed);
            }

            feedResponseDtoList.sort(Comparator.comparing(FeedResponseDto::getPostedAt));

            return ResponseEntity.ok(feedResponseDtoList);

        } else {
            // member search
            List<MemberResponseDto> memberResponseDtoList = new ArrayList<>();

            if (memberRepository.existsByEmail(keyword)) {

                Member member = memberRepository.findByEmail(keyword).orElseThrow(
                        () -> new NullPointerException("해당 검색어에 맞는 사용자가 없습니다.")
                );

                memberResponseDtoList.add(MemberResponseDto.builder()
                        .memberId(member.getId())
                        .profileImage(member.getProfileImage())
                        .nickname(member.getNickname()).build());

            } else if (memberRepository.existsByNickname(keyword)) {

                Member member = memberRepository.findByNickname(keyword).orElseThrow(
                        () -> new NullPointerException("해당 검색어에 맞는 사용자가 없습니다.")
                );

                memberResponseDtoList.add(MemberResponseDto.builder()
                        .memberId(member.getId())
                        .profileImage(member.getProfileImage())
                        .nickname(member.getNickname()).build());
            }

            return ResponseEntity.ok(memberResponseDtoList);
        }
    }

    @Transactional
    public ResponseEntity<?> getFollowingFeeds(MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        List<Follow> followingList = followRepository.findAllByFromMember(memberDetails.getMember());

        List<FeedResponseDto> feedResponseDtoList = new ArrayList<>();

        for (Follow following : followingList) {
            Member toMember = following.getToMember();

            List<Feed> feedList = feedRepository.findAllByMember(toMember);
            for (Feed feed : feedList) {
                addFeed(feedResponseDtoList, feed);
            }
        }
        // add user's own feed too
        for (Feed feed : feedRepository.findAllByMember(memberDetails.getMember())) {
            addFeed(feedResponseDtoList, feed);
        }

        // sort by time created
        feedResponseDtoList.sort(Comparator.comparing(FeedResponseDto::getPostedAt));

        return ResponseEntity.ok(feedResponseDtoList);
    }

    @Transactional
    public ResponseEntity<?> getRecommendedFeeds(MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        List<MemberTagMapper> memberTagMapperList = memberTagMapperRepository.findAllByMember(memberDetails.getMember());

        List<FeedResponseDto> feedResponseDtoList = new ArrayList<>();

        for (MemberTagMapper memberTagMapper : memberTagMapperList){
            Tag tag = memberTagMapper.getTag();

            List<FeedTagMapper> feedTagMapperList = feedTagMapperRepository.findTop5ByTagOrderByIdDesc(tag);

            for (FeedTagMapper feedTagMapper : feedTagMapperList){
                Feed feed = feedTagMapper.getFeed();
                addFeed(feedResponseDtoList, feed);
            }
        }

        feedResponseDtoList.sort(Comparator.comparing(FeedResponseDto::getPostedAt));

        return ResponseEntity.ok(feedResponseDtoList);
    }

    private List<FeedResponseDto> addFeed(List<FeedResponseDto> feedResponseDtoList, Feed feed) {
        // TODO: time complexity for taglist
        // O(total number of tags) or O(number of tags a single feed has)?

        Member member = feed.getMember();

        FeedResponseDto feedResponseDto = FeedResponseDto.builder()
                .feedId(feed.getId())
                .memberId(member.getId())
                .profileImageUrl(member.getProfileImage())
                .nickname(member.getNickname())
                .todoList(feed.getTodoList())
                .feedContent(feed.getFeedContent())
                .feedImagesUrlList(feed.getFeedImageList())
                .tagList(feedTagMapperRepository.findAllByFeed(feed).stream()
                        .map(feedTagMapper1 -> feedTagMapper1.getTag().getTagContent())
                        .collect(Collectors.toList()))
                .reactionResponseDtoList(reactionRepository.findAllByFeed(feed).stream()
                        .map(r -> ReactionResponseDto.builder()
                                .memberId(r.getMember().getId())
                                .nickname(r.getMember().getNickname())
                                .reactionType(r.getReactionType())
                                .build())
                        .collect(Collectors.toList()))
                .commentResponseDtoList(commentRepository.findByFeed(feed).stream()
                        .map(c -> CommentResponseDto.builder()
                                .commentId(c.getId())
                                .memberId(c.getMember().getId())
                                .nickname(c.getMember().getNickname())
                                .commentContent(c.getCommentContent())
                                .postedAt(c.getPostedAt())
                                .build())
                        .collect(Collectors.toList()))
                .postedAt(feed.getPostedAt())
                .build();

        feedResponseDtoList.add(feedResponseDto);

        return feedResponseDtoList;
    }
}
