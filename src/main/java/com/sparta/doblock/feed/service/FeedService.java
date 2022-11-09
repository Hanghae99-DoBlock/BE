package com.sparta.doblock.feed.service;

import com.sparta.doblock.feed.dto.request.DateRequestDto;
import com.sparta.doblock.feed.dto.request.FeedRequestDto;
import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.feed.repository.FeedRepository;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.tag.entity.Tag;
import com.sparta.doblock.tag.mapper.FeedTagMapper;
import com.sparta.doblock.tag.mapper.TodoTagMapper;
import com.sparta.doblock.tag.repository.FeedTagMapperRepository;
import com.sparta.doblock.tag.repository.TagRepository;
import com.sparta.doblock.tag.repository.TodoTagMapperRepository;
import com.sparta.doblock.todo.dto.response.TodoResponseDto;
import com.sparta.doblock.todo.entity.Todo;
import com.sparta.doblock.todo.repository.TodoRepository;
import com.sparta.doblock.util.S3UploadService;
import lombok.RequiredArgsConstructor;
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
    private final TodoTagMapperRepository todoTagMapperRepository;

    private final S3UploadService s3UploadService;

    @Transactional
    public ResponseEntity<?> getTodoByDate(DateRequestDto dateRequestDto, MemberDetailsImpl memberDetails) {
        // TODO: merge with 영성's todo entity/responseDto
        LocalDate date = LocalDate.of(dateRequestDto.getYear(), dateRequestDto.getMonth(), dateRequestDto.getDay());
        List<Todo> todoList = todoRepository.findByMemberAndDate(memberDetails.getMember(), date);

        List<TodoResponseDto> todoResponseDtoList = new ArrayList<>();

        for (Todo todo : todoList) {
            if (!todo.isCompleted()) {
                continue;
            }
            List<String> tagList = new ArrayList<>();
            for (TodoTagMapper todoTagMapper : todoTagMapperRepository.findByTodo(todo)) {
                tagList.add(todoTagMapper.getTag().getContent());
            }
            TodoResponseDto todoResponseDto = TodoResponseDto.builder()
                    .todoId(todo.getId())
                    .todo(todo.getContent())
                    .tagList(tagList)
                    .build();
            todoResponseDtoList.add(todoResponseDto);
        }
        return ResponseEntity.ok(todoResponseDtoList);
    }

    @Transactional
    public ResponseEntity<?> createFeed(FeedRequestDto feedRequestDto, MemberDetailsImpl memberDetails) {
        // member can only post feed related to their own todo's

        if (Objects.isNull(memberDetails)) {
            return new ResponseEntity<>("로그인이 필요합니다", HttpStatus.UNAUTHORIZED);
        }

        List<String> todoList = new ArrayList<>();

        for (Long todoId : feedRequestDto.getTodoIdList()) {
            Todo todo = todoRepository.findById(todoId).orElseThrow(
                    () -> new NullPointerException("해당 투두가 존재하지 않습니다")
            );
            if (! todo.getMember().isEqual(memberDetails.getMember())) {
                return new ResponseEntity<>("투두의 작성자가 아닙니다", HttpStatus.FORBIDDEN);
            } else if (! todo.isCompleted()) {
                return new ResponseEntity<>("투두가 완성되지 않았습니다", HttpStatus.FORBIDDEN);
            } else {
                todoList.add(todo.getContent());
            }
        }

        List<String> feedImageList = feedRequestDto.getFeedImageList().stream()
                .map(s3UploadService::uploadImage)
                .collect(Collectors.toList());

        Feed feed = Feed.builder()
                .member(memberDetails.getMember())
                .todoList(todoList)
                .content(feedRequestDto.getContent())
                .feedImageList(feedImageList)
                .build();

        feedRepository.save(feed);

        // Tag & FeedTagMapper
        for (String tagContent : feedRequestDto.getTagList()) {
            Tag tag = tagRepository.findByContent(tagContent).orElse(Tag.builder().content(tagContent).build());

            tagRepository.save(tag);

            FeedTagMapper feedTagMapper = FeedTagMapper.builder()
                    .feed(feed)
                    .tag(tag)
                    .build();

            feedTagMapperRepository.save(feedTagMapper);
        }

        return ResponseEntity.ok("성공적으로 피드를 생성하였습니다");
    }

    @Transactional
    public ResponseEntity<?> updateFeed(Long feedId, FeedRequestDto feedRequestDto, MemberDetailsImpl memberDetails) {
        if (Objects.isNull(memberDetails)) {
            return new ResponseEntity<>("로그인이 필요합니다", HttpStatus.UNAUTHORIZED);
        }

        List<String> todoList = feedRequestDto.getTodoIdList().stream()
                .map(id -> todoRepository.findById(id).orElseThrow(
                        () -> new NullPointerException("존재하지 않는 투두입니다")
                ).getContent())
                .collect(Collectors.toList());

        List<String> feedImageList = feedRequestDto.getFeedImageList().stream()
                .map(s3UploadService::uploadImage)
                .collect(Collectors.toList());

        Feed feed = feedRepository.findById(feedId).orElseThrow(
                () -> new NullPointerException("존재하는 피드가 아닙니다")
        );

        feed.update(todoList, feedRequestDto.getContent(), feedImageList);

        // delete existing tags and create new ones

        feedTagMapperRepository.deleteAllByFeed(feed);

        for (String tagContent : feedRequestDto.getTagList()) {
            Tag tag = tagRepository.findByContent(tagContent).orElse(Tag.builder().content(tagContent).build());

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
        if (! feed.getMember().isEqual(memberDetails.getMember())) {
            return new ResponseEntity<>("본인이 작성한 피드가 아닙니다", HttpStatus.FORBIDDEN);
        }

        feedTagMapperRepository.deleteAllByFeed(feed);

        feedRepository.delete(feed);

        return ResponseEntity.ok("성공적으로 피드를 삭제하였습니다");
    }

}
