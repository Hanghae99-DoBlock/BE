package com.sparta.doblock.reaction.controller;

import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.reaction.dto.request.ReactionRequestDto;
import com.sparta.doblock.reaction.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feed")
public class ReactionController {
    private final ReactionService reactionService;

    @PostMapping("/{feedId}/reaction")
    public ResponseEntity<?> addReaction(@PathVariable Long feedId, @RequestBody ReactionRequestDto reactionRequestDto,
                                         @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return reactionService.addReaction(feedId, reactionRequestDto, memberDetails);
    }

    @PatchMapping("/{feedId}/reaction")
    public ResponseEntity<?> editReaction(@PathVariable Long feedId, @RequestBody ReactionRequestDto reactionRequestDto,
                                         @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return reactionService.editReaction(feedId, reactionRequestDto, memberDetails);
    }

    @DeleteMapping("/{feedId}/reaction")
    public ResponseEntity<?> deleteReaction(@PathVariable Long feedId, @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return reactionService.deleteReaction(feedId, memberDetails);
    }

    @GetMapping("/{feedId}/recent-reaction")
    public ResponseEntity<?> getRecentReaction(@PathVariable Long feedId) {
        return reactionService.getRecentReaction(feedId);
    }

    @GetMapping("/{feedId}/reaction-list")
    public ResponseEntity<?> getReactionList(@PathVariable Long feedId) {
        return reactionService.getReactionList(feedId);
    }
}
