package com.sparta.doblock.search.service;

import com.sparta.doblock.comment.dto.response.CommentResponseDto;
import com.sparta.doblock.comment.repository.CommentRepository;
import com.sparta.doblock.feed.dto.response.FeedResponseDto;
import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.member.dto.response.MemberResponseDto;
import com.sparta.doblock.member.entity.Member;
import com.sparta.doblock.member.repository.MemberRepository;
import com.sparta.doblock.reaction.dto.response.ReactionResponseDto;
import com.sparta.doblock.reaction.repository.ReactionRepository;
import com.sparta.doblock.tag.entity.Tag;
import com.sparta.doblock.tag.mapper.FeedTagMapper;
import com.sparta.doblock.tag.repository.FeedTagMapperRepository;
import com.sparta.doblock.tag.repository.TagRepository;
import com.sparta.doblock.todo.dto.response.TodoResponseDto;
import com.sparta.doblock.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final MemberRepository memberRepository;
    private final ReactionRepository reactionRepository;
    private final CommentRepository commentRepository;
    private final TagRepository tagRepository;
    private final FeedTagMapperRepository feedTagMapperRepository;

    @Transactional
    public ResponseEntity<?> search(String keyword, String category) {

        if (category.equals("feed")) {
            // feed search
            Tag tag = tagRepository.findByContent(keyword).orElseThrow(
                    () -> new NullPointerException("해당 검색어에 맞는 피드 가 없습니다")
            );
            List<FeedTagMapper> feedTagMapperList = feedTagMapperRepository.findByTag(tag);
            List<FeedResponseDto> feedResponseDtoList = new ArrayList<>();

            for (FeedTagMapper feedTagMapper : feedTagMapperList) {
                Feed feed = feedTagMapper.getFeed();
                Member member = feed.getMember(); // author of the feed
                // TODO: time complexity for taglist
                // O(total number of tags) or O(number of tags a single feed has)?
                FeedResponseDto feedResponseDto = FeedResponseDto.builder()
                        .feedId(feed.getId())
                        .memberId(member.getId())
                        .profileImageUrl(member.getProfileImage())
                        .nickname(member.getNickname())
                        .todoList(feed.getTodoList())
                        .content(feed.getContent())
                        .feedImagesUrlList(feed.getFeedImageList())
                        .tagList(feedTagMapperRepository.findByFeed(feed).stream()
                                .map(feedTagMapper1 -> feedTagMapper1.getTag().getContent())
                                .collect(Collectors.toList()))
                        .reactionResponseDtoList(reactionRepository.findByFeed(feed).stream()
                                .map(r -> ReactionResponseDto.builder()
                                        .nickname(r.getMember().getNickname())
                                        .reactionType(r.getReactionType())
                                        .build())
                                .collect(Collectors.toList()))
                        .commentResponseDtoList(commentRepository.findByFeed(feed).stream()
                                .map(c -> CommentResponseDto.builder()
                                        .nickname(c.getMember().getNickname())
                                        .content(c.getContent())
                                        .build())
                                .collect(Collectors.toList()))
                        .build();
                feedResponseDtoList.add(feedResponseDto);
            }

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
            }

            if (memberRepository.existsByNickname(keyword)) {
                Member member = memberRepository.findByNickname(keyword).orElseThrow(NullPointerException::new);
                memberResponseDtoList.add(MemberResponseDto.builder()
                        .memberId(member.getId())
                        .profileImage(member.getProfileImage())
                        .nickname(member.getNickname()).build());
            }
            return ResponseEntity.ok(memberResponseDtoList);
        }
    }
}
