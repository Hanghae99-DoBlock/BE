package com.sparta.doblock.search.controller;

import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam("keyword") String keyword, @RequestParam("category") String category,
                                    @RequestParam("id") @Nullable Long lastId, @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return searchService.search(keyword, category, lastId, memberDetails);
    }

    @GetMapping("/feed/following")
    public ResponseEntity<?> getFollowingFeeds(@RequestParam("id") @Nullable Long lastId,
                                               @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return searchService.getFollowingFeeds(lastId, memberDetails);
    }

    @GetMapping("/feed/recommended")
    public ResponseEntity<?> getRecommendedFeeds(@RequestParam("id") @Nullable Long lastId,
                                                 @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return searchService.getRecommendedFeeds(lastId, memberDetails);
    }

    @GetMapping("/profile/{memberId}/feed")
    public ResponseEntity<?> getUserFeeds(@PathVariable(name = "memberId") Long memberId, @RequestParam("id") @Nullable Long lastId){
        return searchService.getUserFeeds(memberId, lastId);
    }

    @GetMapping("/feed/{feed_id}")
    public ResponseEntity<?> getFeed(@PathVariable(name = "feed_id") Long feedId, @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return searchService.getFeed(feedId, memberDetails);
    }
}
