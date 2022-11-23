package com.sparta.doblock.search.controller;

import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam("keyword") String keyword, @RequestParam("category") String category, @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        // Search by tag
        return searchService.search(keyword, category, memberDetails);
    }

    @GetMapping("/feed/following")
    public ResponseEntity<?> getFollowingFeeds(@AuthenticationPrincipal MemberDetailsImpl memberDetails,
                                               @RequestParam("page") int page) {
        return searchService.getFollowingFeeds(memberDetails, page);
    }

    @GetMapping("/feed/recommended")
    public ResponseEntity<?> getRecommendedFeeds(@AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return searchService.getRecommendedFeeds(memberDetails);
    }

    @GetMapping("/feed/{feed_id}")
    public ResponseEntity<?> getFeed(@PathVariable(name = "feed_id") Long feedId, @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return searchService.getFeed(feedId, memberDetails);
    }
}
