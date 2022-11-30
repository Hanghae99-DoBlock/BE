package com.sparta.doblock.notification.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {

    SseEmitter save(String emitterId, SseEmitter sseEmitter);
    void saveEventCache(String eventCacheId, Object event);
    Map<String, SseEmitter> findAllEmitterStartWithByMemberId(Long memberId);
    Map<String, Object> findAllEventCacheStartWithByMemberId(Long memberId);
    void deleteByEmitterId(String emitterId);
    void deleteAllEmitterStartWithMemberId(Long memberId);
    void deleteAllEventCacheStartWithMemberId(Long memberId);
}
