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
import com.sparta.doblock.tag.repository.FeedTagMapperRepository;
import com.sparta.doblock.tag.repository.TagRepository;
import com.sparta.doblock.todo.dto.response.TodoResponseDto;
import com.sparta.doblock.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
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

    @Transactional
    public ResponseEntity<?> search(String keyword, String category) {

        if (category.equals("feed")) {
            // feed search
            Tag tag = tagRepository.findByTagContent(keyword).orElseThrow(
                    () -> new NullPointerException("해당 검색어에 맞는 피드 가 없습니다")
            );

            List<FeedTagMapper> feedTagMapperList = feedTagMapperRepository.findByTag(tag);
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

                Member member = memberRepository.findByEmail(keyword).orElseThrow(NullPointerException::new);

                memberResponseDtoList.add(MemberResponseDto.builder()
                        .memberId(member.getId())
                        .profileImage(member.getProfileImage())
                        .nickname(member.getNickname()).build());

            } else if (memberRepository.existsByNickname(keyword)) {

                Member member = memberRepository.findByNickname(keyword).orElseThrow(NullPointerException::new);

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
            return new ResponseEntity<>("로그인이 필요합니다", HttpStatus.UNAUTHORIZED);
        }

        List<Follow> followingList = followRepository.findAllByFromMember(memberDetails.getMember());

        List<FeedResponseDto> feedResponseDtoList = new ArrayList<>();

        for (Follow following : followingList) {
            Member toMember = following.getToMember();

            List<Feed> feedList = feedRepository.findByMember(toMember);
            for (Feed feed : feedList) {
                addFeed(feedResponseDtoList, feed);
            }
        }
        // add user's own feed too
        for (Feed feed : feedRepository.findByMember(memberDetails.getMember())) {
            addFeed(feedResponseDtoList, feed);
        }

        // sort by time created
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
                .tagList(feedTagMapperRepository.findByFeed(feed).stream()
                        .map(feedTagMapper1 -> feedTagMapper1.getTag().getTagContent())
                        .collect(Collectors.toList()))
                .reactionResponseDtoList(reactionRepository.findAllByFeed(feed).stream()
                        .map(r -> ReactionResponseDto.builder()
                                .nickname(r.getMember().getNickname())
                                .reactionType(r.getReactionType())
                                .build())
                        .collect(Collectors.toList()))
                .commentResponseDtoList(commentRepository.findByFeed(feed).stream()
                        .map(c -> CommentResponseDto.builder()
                                .commentId(c.getId())
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