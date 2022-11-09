package com.sparta.doblock.feed.controller;

import com.sparta.doblock.feed.dto.request.DateRequestDto;
import com.sparta.doblock.feed.dto.request.FeedRequestDto;
import com.sparta.doblock.feed.service.FeedService;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feed")
public class FeedController {
    private final FeedService feedService;

    @GetMapping("")
    public ResponseEntity<?> getTodoByDate(@RequestBody DateRequestDto dateRequestDto, @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return feedService.getTodoByDate(dateRequestDto, memberDetails);
    }

    // Need to use @ModelAttribute for form data
    @PostMapping("")
    public ResponseEntity<?> createFeed(@ModelAttribute FeedRequestDto feedRequestDto, @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return feedService.createFeed(feedRequestDto, memberDetails);
    }

    @PatchMapping("/{feedId}")
    public ResponseEntity<?> updateFeed(@PathVariable Long feedId, @ModelAttribute FeedRequestDto feedRequestDto,
                                        @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return feedService.updateFeed(feedId, feedRequestDto, memberDetails);
    }

    @DeleteMapping("/{feedId}")
    public ResponseEntity<?> deleteFeed(@PathVariable Long feedId, @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return feedService.deleteFeed(feedId, memberDetails);
    }
}
