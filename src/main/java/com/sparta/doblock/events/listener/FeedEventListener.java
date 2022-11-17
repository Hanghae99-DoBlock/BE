package com.sparta.doblock.events.listener;

import com.sparta.doblock.events.entity.FeedEvents;
import com.sparta.doblock.feed.entity.Feed;
import com.sparta.doblock.feed.repository.FeedRepository;
import com.sparta.doblock.tag.entity.Tag;
import com.sparta.doblock.tag.mapper.FeedTagMapper;
import com.sparta.doblock.tag.repository.FeedTagMapperRepository;
import com.sparta.doblock.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FeedEventListener {

    private final FeedRepository feedRepository;
    private final TagRepository tagRepository;
    private final FeedTagMapperRepository feedTagMapperRepository;

    @Transactional
    @EventListener(classes = FeedEvents.class)
    public ResponseEntity<?> createEventFeed(FeedEvents feedEvents){

        List<String> tagList = new ArrayList<>();
        tagList.add("두블럭");
        tagList.add("뱃지획득!");
        tagList.add("축하합니다 ㅇ_ㅇb");
        tagList.add("사랑해주셔서");
        tagList.add("감사합니다!");

        Feed feed = Feed.builder()
                .member(feedEvents.getMember())
                .feedContent(feedEvents.getMember() + "님이" + feedEvents.getBadgeType() + "뱃지를 얻으셨습니다! 다들 축하해주세요!")
                .eventFeed(true)
                .build();

        feedRepository.save(feed);

        for (String tagContent : tagList) {
            Tag tag = tagRepository.findByTagContent(tagContent).orElse(Tag.builder().tagContent(tagContent).build());

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
