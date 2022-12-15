package com.sparta.doblock.search.service;

import com.sparta.doblock.comment.repository.CommentRepository;
import com.sparta.doblock.events.entity.Badges;
import com.sparta.doblock.events.repository.BadgesRepository;
import com.sparta.doblock.exception.DoBlockExceptions;
import com.sparta.doblock.exception.ErrorCodes;
import com.sparta.doblock.feed.dto.response.FeedResponseDto;
import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.feed.repository.FeedRepository;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.member.repository.MemberRepository;
import com.sparta.doblock.profile.dto.response.FollowResponseDto;
import com.sparta.doblock.profile.entity.Follow;
import com.sparta.doblock.profile.repository.FollowRepository;
import com.sparta.doblock.reaction.dto.response.ReactionResponseDto;
import com.sparta.doblock.reaction.entity.Reaction;
import com.sparta.doblock.reaction.repository.ReactionRepository;
import com.sparta.doblock.tag.mapper.MemberTagMapper;
import com.sparta.doblock.tag.repository.FeedTagMapperRepository;
import com.sparta.doblock.tag.repository.MemberTagMapperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    private final FeedTagMapperRepository feedTagMapperRepository;
    private final MemberTagMapperRepository memberTagMapperRepository;
    private final BadgesRepository badgesRepository;

    public ResponseEntity<?> search(String keyword, String category, Long lastId, MemberDetailsImpl memberDetails) {

        if (category.equals("feed")) {

            List<FeedResponseDto> feedResponseDtoList = new ArrayList<>();

            for (Feed feed : feedTagMapperRepository.searchAllByFeedTagLike(lastId, keyword)){
                addFeed(feedResponseDtoList, feed);
            }

            return ResponseEntity.ok(feedResponseDtoList);

        } else {

            List<FollowResponseDto> followResponseDtoList = new ArrayList<>();

            for (Member member : memberRepository.searchAllByEmailOrNickname(lastId, keyword)) {
                followResponseDtoList.add(
                        FollowResponseDto.builder()
                                .memberId(member.getId())
                                .profileImage(member.getProfileImage())
                                .nickname(member.getNickname())
                                .email(member.getEmail())
                                .followOrNot(followRepository.existsByFromMemberAndToMember(memberDetails.getMember(), member))
                                .build()
                );
            }

            return ResponseEntity.ok(followResponseDtoList);
        }
    }

    public ResponseEntity<?> getFollowingFeeds(Long lastId, MemberDetailsImpl memberDetails) {

        List<Follow> followingList = followRepository.findAllByFromMember(memberDetails.getMember());
        List<FeedResponseDto> feedResponseDtoList = new ArrayList<>();

        for (Feed feed : feedRepository.searchAllByFollow(lastId, memberDetails.getMember(), followingList)) {
            addFeed(feedResponseDtoList, feed);
        }

        return ResponseEntity.ok(feedResponseDtoList);
    }

    public ResponseEntity<?> getRecommendedFeeds(Long lastId, MemberDetailsImpl memberDetails) {

        List<MemberTagMapper> memberTagMapperList = memberTagMapperRepository.findAllByMember(memberDetails.getMember());
        List<FeedResponseDto> feedResponseDtoList = new ArrayList<>();

        for (Feed feed : feedTagMapperRepository.searchAllByMemberTagLike(lastId, memberTagMapperList)) {
            addFeed(feedResponseDtoList, feed);
        }

        return ResponseEntity.ok(feedResponseDtoList);
    }

    public ResponseEntity<?> getUserFeeds(Long memberId, Long lastId) {

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_MEMBER)
        );

        List<FeedResponseDto> feedResponseDtoList = new ArrayList<>();

        for (Feed feed : feedRepository.searchAllByMember(lastId, member)) {
            addFeed(feedResponseDtoList, feed);
        }

        return ResponseEntity.ok(feedResponseDtoList);
    }

    public ResponseEntity<?> getFeed(Long feedId, MemberDetailsImpl memberDetails) {

        Feed feed = feedRepository.findById(feedId).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_FEED)
        );

        Member member = feed.getMember();
        Badges badges = badgesRepository.findByMemberAndSelectedBadge(member, true).orElse(null);
        Reaction reaction = reactionRepository.findByFeedAndMember(feed, memberDetails.getMember()).orElse(null);

        FeedResponseDto feedResponseDto = FeedResponseDto.builder()
                .feedId(feed.getId())
                .memberId(member.getId())
                .profileImageUrl(member.getProfileImage())
                .nickname(member.getNickname())
                .badgeName(badges != null ? badges.getBadgeType().getBadgeName() : null)
                .followOrNot(followRepository.existsByFromMemberAndToMember(memberDetails.getMember(), member))
                .todoList(feed.getTodoList())
                .feedTitle(feed.getFeedTitle())
                .feedContent(feed.getFeedContent())
                .feedImagesUrlList(feed.getFeedImageList())
                .feedColor(feed.getFeedColor())
                .eventFeed(feed.isEventFeed())
                .countReaction(reactionRepository.countAllByFeed(feed))
                .myReaction(reactionRepository.existsByFeedAndMember(feed, memberDetails.getMember()))
                .myReactionType(reaction != null ? reaction.getReactionType() : null)
                .currentReactionType(reactionRepository.findTop2ByFeedOrderByModifiedAtDesc(feed).stream()
                        .map(r -> ReactionResponseDto.builder()
                                .memberId(r.getMember().getId())
                                .reactionType(r.getReactionType())
                                .build())
                        .collect(Collectors.toList()))
                .tagList(feedTagMapperRepository.findAllByFeed(feed).stream()
                        .map(feedTagMapper1 -> feedTagMapper1.getTag().getTagContent())
                        .collect(Collectors.toList()))
                .reactionResponseDtoList(reactionRepository.findAllByFeedOrderByModifiedAtDesc(feed).stream()
                        .map(r -> ReactionResponseDto.builder()
                                .memberId(r.getMember().getId())
                                .profileImage(r.getMember().getProfileImage())
                                .nickname(r.getMember().getNickname())
                                .email(r.getMember().getEmail())
                                .reactionType(r.getReactionType())
                                .build())
                        .collect(Collectors.toList()))
                .countComment(commentRepository.countAllByFeed(feed))
                .postedAt(feed.getPostedAt())
                .build();

        return ResponseEntity.ok(feedResponseDto);
    }

    private void addFeed(List<FeedResponseDto> feedResponseDtoList, Feed feed) {

        Member member = feed.getMember();

        FeedResponseDto feedResponseDto = FeedResponseDto.builder()
                .feedId(feed.getId())
                .memberId(member.getId())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImage())
                .todoList(feed.getTodoList())
                .feedTitle(feed.getFeedTitle())
                .feedColor(feed.getFeedColor())
                .eventFeed(feed.isEventFeed())
                .countComment(commentRepository.countAllByFeed(feed))
                .countReaction(reactionRepository.countAllByFeed(feed))
                .tagList(feedTagMapperRepository.findAllByFeed(feed).stream()
                        .map(feedTagMapper1 -> feedTagMapper1.getTag().getTagContent())
                        .collect(Collectors.toList()))
                .postedAt(feed.getPostedAt())
                .build();

        feedResponseDtoList.add(feedResponseDto);
    }
}
