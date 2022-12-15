package com.sparta.doblock.notification.controller;

import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sse")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal MemberDetailsImpl memberDetails,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId){
        return notificationService.subscribe(memberDetails, lastEventId);
    }
}
