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
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        List<String> todoList = new ArrayList<>();

        for (Long todoId : feedRequestDto.getTodoIdList()) {
            Todo todo = todoRepository.findById(todoId).orElseThrow(
                    () -> new NullPointerException("존재하지 않는 todo 입니다")
            );
//            System.out.println(todo.getMember().getId() + " " + memberDetails.getMember().getId());
            if (! todo.getMember().isEqual(memberDetails.getMember())) {
                return new ResponseEntity("본인 todo 가 아닙니다", HttpStatus.UNAUTHORIZED);
            }
            todoList.add(todo.getContent());
        }

        List<String> feedImageList = new ArrayList<>();

//        System.out.println(feedRequestDto.getFeedImage1());
//        System.out.println(feedRequestDto.getFeedImage2());
//        System.out.println(feedRequestDto.getFeedImage3());
//        System.out.println(feedRequestDto.getFeedImage4());

        for (MultipartFile image : getImageList(feedRequestDto)) {
//            String imageUrl = image.toString();
            // TODO: FIX S3 ISSUE
            String imageUrl = s3UploadService.uploadImage(image);
            feedImageList.add(imageUrl);
        }

        System.out.println("feedImageList Generated");

        Feed feed = Feed.builder()
                .member(memberDetails.getMember())
                .todoList(todoList)
                .content(feedRequestDto.getContent())
                .feedImageList(feedImageList)
                .build();

        feedRepository.save(feed);

        for (String tagContent : feedRequestDto.getTagList()) {
            Tag tag = tagRepository.findByContent(tagContent).orElse(Tag.builder().content(tagContent).build());

            tagRepository.save(tag);

            FeedTagMapper feedTagMapper = FeedTagMapper.builder()
                    .feed(feed)
                    .tag(tag)
                    .build();

            feedTagMapperRepository.save(feedTagMapper);
        }

        return ResponseEntity.ok(feed);
    }

    @Transactional
    public ResponseEntity<?> updateFeed(Long feedId, FeedRequestDto feedRequestDto, MemberDetailsImpl memberDetails) {
        List<String> todoList = new ArrayList<>();

        for (Long todoId : feedRequestDto.getTodoIdList()) {
            Todo todo = todoRepository.findById(todoId).orElseThrow(
                    () -> new NullPointerException("존재하지 않는 todo 입니다")
            );
            if (! todo.getMember().isEqual(memberDetails.getMember())) {
                return new ResponseEntity("본인 todo 가 아닙니다", HttpStatus.UNAUTHORIZED);
            }
            todoList.add(todo.getContent());
        }

        List<String> feedImageList = new ArrayList<>();

        for (MultipartFile image : getImageList(feedRequestDto)) {
//            String imageUrl = image.toString();
            // TODO: FIX S3 ISSUE
            String imageUrl = s3UploadService.uploadImage(image);
            feedImageList.add(imageUrl);
        }

        Feed feed = feedRepository.findById(feedId).orElseThrow(
                () -> new NullPointerException("존재하는 피드가 아닙니다")
        );

        feed.update(todoList, feedRequestDto.getContent(), feedImageList);

        // delete existing tags and create new ones

        List<FeedTagMapper> feedTagMapperList = feedTagMapperRepository.findByFeed(feed);
        feedTagMapperRepository.deleteAll(feedTagMapperList);

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
            return new ResponseEntity("본인이 작성한 피드가 아닙니다", HttpStatus.FORBIDDEN);
        }
        List<FeedTagMapper> feedTagMapperList = feedTagMapperRepository.findByFeed(feed);
        feedTagMapperRepository.deleteAll(feedTagMapperList);

        feedRepository.delete(feed);

        return ResponseEntity.ok("성공적으로 피드를 삭제하였습니다");
    }

    private List<MultipartFile> getImageList(FeedRequestDto feedRequestDto) {
        List<MultipartFile> imageList = new ArrayList<>();

        // add images manually
        if (! Objects.isNull(feedRequestDto.getFeedImage1())) {
            imageList.add(feedRequestDto.getFeedImage1());
        }
        if (! Objects.isNull(feedRequestDto.getFeedImage2())) {
            imageList.add(feedRequestDto.getFeedImage2());
        }
        if (! Objects.isNull(feedRequestDto.getFeedImage3())) {
            imageList.add(feedRequestDto.getFeedImage3());
        }
        if (! Objects.isNull(feedRequestDto.getFeedImage4())) {
            imageList.add(feedRequestDto.getFeedImage4());
        }

        return imageList;
    }
}
