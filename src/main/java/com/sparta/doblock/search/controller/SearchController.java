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
    public ResponseEntity<?> search(@RequestParam("keyword") String keyword, @RequestParam("category") String category,
                                    @RequestParam("page") int page, @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        // Search by tag
        return searchService.search(keyword, category, page, memberDetails);
    }

    @GetMapping("/feed/following")
    public ResponseEntity<?> getFollowingFeeds(@RequestParam("page") int page,
                                               @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return searchService.getFollowingFeeds(page, memberDetails);
    }

    @GetMapping("/feed/recommended")
    public ResponseEntity<?> getRecommendedFeeds(@RequestParam("page") int page,
                                                 @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return searchService.getRecommendedFeeds(page, memberDetails);
    }

    @GetMapping("/profile/{memberId}/feed")
    public ResponseEntity<?> getUserFeeds(@PathVariable(name = "memberId") Long memberId, @RequestParam("page") int page){
        return searchService.getUserFeeds(memberId, page);
    }

    @GetMapping("/feed/{feed_id}")
    public ResponseEntity<?> getFeed(@PathVariable(name = "feed_id") Long feedId, @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return searchService.getFeed(feedId, memberDetails);
    }
}
