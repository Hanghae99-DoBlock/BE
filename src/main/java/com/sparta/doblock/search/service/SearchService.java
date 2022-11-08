package com.sparta.doblock.search.service;

import com.sparta.doblock.tag.repository.FeedTagMapperRepository;
import com.sparta.doblock.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final TagRepository tagRepository;
    private final FeedTagMapperRepository feedTagMapperRepository;

//    public ResponseEntity<?> search(String keyword, String category) {
//
//        if (category.equals("feed")) {
//            Tag tag = tagRepository.findByContent(keyword).orElseThrow(
//                    () -> new NullPointerException("해당 검색어에 맞는 피드 가 없습니다")
//            );
//            List<FeedTagMapper> feedTagMapperList = feedTagMapperRepository.findByTag(tag);
//
//
//        } else {
//
//        }
//    }
}
