package com.sparta.doblock.search.controller;

import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {
    private final SearchService searchService;

    @GetMapping("")
    public ResponseEntity<?> search(@RequestParam("keyword") String keyword, @RequestParam("category") String category) {
        // Search by tag
        return searchService.search(keyword, category);
    }

    @GetMapping("")
    public ResponseEntity<?> getFollowerFeeds(@AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return searchService.getFollowerFeeds(memberDetails);
    }
}
