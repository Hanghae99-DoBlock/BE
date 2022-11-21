package com.sparta.doblock.feed.service;

import com.sparta.doblock.events.entity.BadgeEvents;
import com.sparta.doblock.feed.dto.request.FeedRequestDto;
import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.feed.repository.FeedRepository;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
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
import org.springframework.http.HttpStatus;
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
    private final TodoDateRepository todoDateRepository;
    private final S3UploadService s3UploadService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public ResponseEntity<?> getTodoByDate(int year, int month, int day, MemberDetailsImpl memberDetails) {

        LocalDate date = LocalDate.of(year, month, day);

        TodoDate todoDate = todoDateRepository.findByDate(date).orElseThrow(
                () -> new NullPointerException("해당 날짜에 등록된 투두가 없습니다")
        );

        List<Todo> todoList = todoRepository.findAllByMemberAndTodoDate(memberDetails.getMember(), todoDate);
        List<TodoResponseDto> todoResponseDtoList = new ArrayList<>();

        for (Todo todo : todoList) {
            if (!todo.isCompleted()) {
                continue;
            }

            TodoResponseDto todoResponseDto = TodoResponseDto.builder()
                    .todoId(todo.getId())
                    .todoContent(todo.getTodoContent())
                    .build();

            todoResponseDtoList.add(todoResponseDto);
        }

        return ResponseEntity.ok(todoResponseDtoList);
    }

    @Transactional
    public ResponseEntity<?> createFeed(FeedRequestDto feedRequestDto, MemberDetailsImpl memberDetails) {

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        if (feedRequestDto.getFeedContent().length() >= 101) {
            throw new RuntimeException("피드 내용은 최대 100자까지 입력 가능합니다.");
        }

        if (feedRequestDto.getFeedImageList().size() >= 5){
            throw new RuntimeException("사진은 피드 당 4개까지 가능합니다.");
        }

        List<String> todoList = new ArrayList<>();

        for (Long todoId : feedRequestDto.getTodoIdList()) {
            Todo todo = todoRepository.findById(todoId).orElseThrow(
                    () -> new NullPointerException("해당 투두가 존재하지 않습니다")
            );

            if (!todo.getMember().getId().equals(memberDetails.getMember().getId())) {
                return new ResponseEntity<>("투두의 작성자가 아닙니다", HttpStatus.FORBIDDEN);

            } else if (!todo.isCompleted()) {
                return new ResponseEntity<>("투두가 완성되지 않았습니다", HttpStatus.FORBIDDEN);

            } else {
                todoList.add(todo.getTodoContent());
            }
        }

        List<String> feedImageList;

        try {
            feedImageList = feedRequestDto.getFeedImageList().stream()
                    .map(s3UploadService::uploadImage)
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
            Tag tag = tagRepository.findByTagContent(tagContent).orElse(Tag.builder().tagContent(tagContent).build());

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

        if (Objects.isNull(memberDetails)) {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        if (feedRequestDto.getFeedContent().length() >= 101) {
            throw new RuntimeException("피드 내용은 최대 100자까지 입력 가능합니다.");
        }

        Feed feed = feedRepository.findById(feedId).orElseThrow(
                () -> new NullPointerException("존재하는 피드가 아닙니다")
        );

        feed.update(feedRequestDto.getFeedTitle(), feedRequestDto.getFeedContent(), feedRequestDto.getFeedColor());

        feedTagMapperRepository.deleteAllByFeed(feed);

        for (String tagContent : feedRequestDto.getTagList()) {
            Tag tag = tagRepository.findByTagContent(tagContent).orElse(Tag.builder().tagContent(tagContent).build());

            tagRepository.save(tag);

            if (! feedTagMapperRepository.existsByFeedAndTag(feed, tag)) {
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
                () -> new NullPointerException("해당 피드가 없습니다")
        );

        if (!feed.getMember().getId().equals(memberDetails.getMember().getId())) {
            throw new RuntimeException("본인이 작성한 피드가 아닙니다.");
        }

        for (String imageUrl : feed.getFeedImageList()){
            s3UploadService.delete(imageUrl);
        }

        feedTagMapperRepository.deleteAllByFeed(feed);

        feedRepository.delete(feed);

        return ResponseEntity.ok("성공적으로 피드를 삭제하였습니다");
    }
}
