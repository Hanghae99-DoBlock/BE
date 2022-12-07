package com.sparta.doblock.feed.service;

import com.sparta.doblock.comment.repository.CommentRepository;
import com.sparta.doblock.events.entity.BadgeEvents;
import com.sparta.doblock.exception.DoBlockExceptions;
import com.sparta.doblock.exception.ErrorCodes;
import com.sparta.doblock.feed.dto.request.FeedRequestDto;
import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.feed.repository.FeedRepository;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.profile.dto.request.BadgesRequestDto;
import com.sparta.doblock.reaction.repository.ReactionRepository;
import com.sparta.doblock.tag.entity.Tag;
import com.sparta.doblock.tag.mapper.FeedTagMapper;
import com.sparta.doblock.tag.repository.FeedTagMapperRepository;
import com.sparta.doblock.tag.repository.TagRepository;
import com.sparta.doblock.todo.dto.response.TodoResponseDto;
import com.sparta.doblock.todo.entity.Todo;
import com.sparta.doblock.todo.repository.TodoRepository;
import com.sparta.doblock.todo.entity.TodoDate;
import com.sparta.doblock.todo.repository.TodoDateRepository;
import com.sparta.doblock.util.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final TodoRepository todoRepository;
    private final FeedRepository feedRepository;
    private final TagRepository tagRepository;
    private final FeedTagMapperRepository feedTagMapperRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;
    private final TodoDateRepository todoDateRepository;
    private final S3UploadService s3UploadService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public ResponseEntity<?> getTodoByDate(int year, int month, int day, MemberDetailsImpl memberDetails) {

        LocalDate date = LocalDate.of(year, month, day);

        TodoDate todoDate = todoDateRepository.findByDateAndMember(date, memberDetails.getMember()).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_TODO_DATE)
        );

        List<Todo> todoList = todoRepository.findAllByMemberAndTodoDate(memberDetails.getMember(), todoDate);
        List<TodoResponseDto> todoResponseDtoList = new ArrayList<>();

        for (Todo todo : todoList) {
            if (!todo.isCompleted()) {
                continue;
            }

            todoResponseDtoList.add(
                    TodoResponseDto.builder()
                            .todoId(todo.getId())
                            .todoContent(todo.getTodoContent())
                            .build()
            );
        }

        return ResponseEntity.ok(todoResponseDtoList);
    }

    @Transactional
    public ResponseEntity<?> createFeed(FeedRequestDto feedRequestDto, MemberDetailsImpl memberDetails) {

        if (feedRequestDto.getFeedContent().length() >= 101) {
            throw new DoBlockExceptions(ErrorCodes.EXCEED_FEED_CONTENT);
        }

        if (feedRequestDto.getFeedImageList().size() >= 5) {
            throw new DoBlockExceptions(ErrorCodes.EXCEED_FEED_IMAGE);
        }

        List<String> todoList = new ArrayList<>();

        for (Long todoId : feedRequestDto.getTodoIdList()) {
            Todo todo = todoRepository.findById(todoId).orElseThrow(
                    () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_TODO)
            );

            if (!todo.getMember().getId().equals(memberDetails.getMember().getId())) {
                throw new DoBlockExceptions(ErrorCodes.NOT_VALID_WRITER);

            } else if (!todo.isCompleted()) {
                throw new DoBlockExceptions(ErrorCodes.NOT_COMPLETED_TODO);

            } else {
                todoList.add(todo.getTodoContent());
            }
        }

        List<String> feedImageList;

        try {
            feedImageList = feedRequestDto.getFeedImageList().stream()
                    .map(s3UploadService::uploadFeedImage)
                    .collect(Collectors.toList());
        } catch (NullPointerException e) {
            feedImageList = new ArrayList<>();
        }

        Feed feed = Feed.builder()
                .member(memberDetails.getMember())
                .todoList(todoList)
                .feedTitle(feedRequestDto.getFeedTitle())
                .feedContent(feedRequestDto.getFeedContent())
                .feedImageList(feedImageList)
                .feedColor(feedRequestDto.getFeedColor())
                .build();

        feedRepository.save(feed);

        for (String tagContent : feedRequestDto.getTagList()) {
            Tag tag = tagRepository.findByTagContent(tagContent).orElse(
                    Tag.builder()
                            .tagContent(tagContent)
                            .build()
            );

            tagRepository.save(tag);

            FeedTagMapper feedTagMapper = FeedTagMapper.builder()
                    .feed(feed)
                    .tag(tag)
                    .build();

            feedTagMapperRepository.save(feedTagMapper);
        }

        applicationEventPublisher.publishEvent(new BadgeEvents.CreateFeedBadgeEvent(memberDetails));

        return ResponseEntity.ok("성공적으로 피드를 생성하였습니다");
    }

    @Transactional
    public ResponseEntity<?> updateFeed(Long feedId, FeedRequestDto feedRequestDto, MemberDetailsImpl memberDetails) {

        if (feedRequestDto.getFeedContent().length() >= 101) {
            throw new DoBlockExceptions(ErrorCodes.EXCEED_FEED_CONTENT);
        }

        Feed feed = feedRepository.findById(feedId).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_FEED)
        );

        if (!feed.getMember().getId().equals(memberDetails.getMember().getId())) {
            throw new DoBlockExceptions(ErrorCodes.NOT_VALID_WRITER);
        }

        feed.update(feedRequestDto.getFeedTitle(), feedRequestDto.getFeedContent(), feedRequestDto.getFeedColor());

        feedTagMapperRepository.deleteAllByFeed(feed);

        for (String tagContent : feedRequestDto.getTagList()) {
            Tag tag = tagRepository.findByTagContent(tagContent).orElse(
                    Tag.builder()
                            .tagContent(tagContent)
                            .build()
            );

            tagRepository.save(tag);

            if (!feedTagMapperRepository.existsByFeedAndTag(feed, tag)) {
                FeedTagMapper feedTagMapper = FeedTagMapper.builder()
                        .feed(feed)
                        .tag(tag)
                        .build();

                feedTagMapperRepository.save(feedTagMapper);
            }
        }

        return ResponseEntity.ok("피드를 성공적으로 수정하였습니다");
    }

    @Transactional
    public ResponseEntity<?> deleteFeed(Long feedId, MemberDetailsImpl memberDetails) {

        Feed feed = feedRepository.findById(feedId).orElseThrow(
                () -> new DoBlockExceptions(ErrorCodes.NOT_FOUND_FEED)
        );

        if (!feed.getMember().getId().equals(memberDetails.getMember().getId())) {
            throw new DoBlockExceptions(ErrorCodes.NOT_VALID_WRITER);
        }

        for (String imageUrl : feed.getFeedImageList()) {
            s3UploadService.delete(imageUrl);
        }

        commentRepository.deleteAllByFeed(feed);

        reactionRepository.deleteAllByFeed(feed);

        feedTagMapperRepository.deleteAllByFeed(feed);

        feedRepository.delete(feed);

        return ResponseEntity.ok("성공적으로 피드를 삭제하였습니다");
    }

    @Transactional
    public ResponseEntity<?> createEventFeed(BadgesRequestDto badgesRequestDto, MemberDetailsImpl memberDetails) {

        List<String> tagList = new ArrayList<>();
        tagList.add("두블럭");
        tagList.add("뱃지획득!");
        tagList.add("축하합니다 ㅇ_ㅇb");
        tagList.add("사랑해주셔서");
        tagList.add("감사합니다!");

        List<String> eventImages = new ArrayList<>();
        eventImages.add(badgesRequestDto.getBadgeType().getBadgeImage());

        Feed feed = Feed.builder()
                .member(memberDetails.getMember())
                .feedTitle(memberDetails.getMember().getNickname() + "님이 뱃지를 획득했습니다!")
                .feedContent(memberDetails.getMember().getNickname() + "님이" + badgesRequestDto.getBadgeType().getBadgeName() + "뱃지를 얻으셨습니다! 다들 축하해주세요!")
                .feedImageList(eventImages)
                .eventFeed(true)
                .build();

        feedRepository.save(feed);

        for (String tagContent : tagList) {
            Tag tag = tagRepository.findByTagContent(tagContent).orElse(
                    Tag.builder()
                            .tagContent(tagContent)
                            .build()
            );

            tagRepository.save(tag);

            FeedTagMapper feedTagMapper = FeedTagMapper.builder()
                    .feed(feed)
                    .tag(tag)
                    .build();

            feedTagMapperRepository.save(feedTagMapper);
        }

        return ResponseEntity.ok("이벤트 피드가 생성되었습니다!");
    }
}
