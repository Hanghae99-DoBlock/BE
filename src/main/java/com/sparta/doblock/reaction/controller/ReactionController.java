package com.sparta.doblock.reaction.controller;

import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.reaction.dto.request.ReactionRequestDto;
import com.sparta.doblock.reaction.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feed")
public class ReactionController {
    private final ReactionService reactionService;

    @PostMapping("/{feedId}/reaction")
    public ResponseEntity<?> addReaction(@PathVariable Long feedId, ReactionRequestDto reactionRequestDto,
                                         @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return reactionService.addReaction(feedId, reactionRequestDto, memberDetails);
    }
}
