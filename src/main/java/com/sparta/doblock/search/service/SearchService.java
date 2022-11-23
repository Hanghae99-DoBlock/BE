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
import org.springframework.http.HttpStatus;
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

    private final int POST_PER_PAGE = 5;

    @Transactional
    public ResponseEntity<?> search(String keyword, String category) {

        if (category.equals("feed")) {

            List<Tag> tagList = tagRepository.searchByTagLike(keyword);
            List<FeedResponseDto> feedResponseDtoList = new ArrayList<>();

            for (Tag tag : tagList) {
                List<FeedTagMapper> feedTagMapperList = feedTagMapperRepository.findAllByTag(tag);

                for (FeedTagMapper feedTagMapper : feedTagMapperList) {
                    Feed feed = feedTagMapper.getFeed();
                    addFeed(feedResponseDtoList, feed);
                }
            }

            feedResponseDtoList.sort(Comparator.comparing(FeedResponseDto::getPostedAt));

            return ResponseEntity.ok(feedResponseDtoList);

        } else {
            // member search
            List<MemberResponseDto> memberResponseDtoList = new ArrayList<>();
            Set<Long> searched = new HashSet<>();

            for (Member member : memberRepository.searchByEmailLike(keyword)) {
                memberResponseDtoList.add(MemberResponseDto.builder()
                        .memberId(member.getId())
                        .profileImage(member.getProfileImage())
                        .nickname(member.getNickname()).build());

                searched.add(member.getId());
            }

            for (Member member : memberRepository.searchByNicknameLike(keyword)) {
                if (searched.contains(member.getId())) {
                    continue;
                }
                memberResponseDtoList.add(MemberResponseDto.builder()
                        .memberId(member.getId())
                        .profileImage(member.getProfileImage())
                        .nickname(member.getNickname()).build());
            }

            return ResponseEntity.ok(memberResponseDtoList);
        }
    }

    @Transactional
    public ResponseEntity<?> getFollowingFeeds(MemberDetailsImpl memberDetails, int page) {

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
        feedResponseDtoList.sort((o1, o2) -> o2.getPostedAt().compareTo(o1.getPostedAt()));

        int startIdx = page * POST_PER_PAGE;
        int endIdx = Math.min(feedResponseDtoList.size(), (page + 1) * POST_PER_PAGE);

        if (endIdx <= startIdx) {
            return new ResponseEntity<>("해당 페이지에 포스트가 없습니다", HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(feedResponseDtoList.subList(startIdx, endIdx));
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
