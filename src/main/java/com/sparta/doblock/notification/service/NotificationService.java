package com.sparta.doblock.notification.service;

import com.sparta.doblock.events.entity.BadgeEvents;
import com.sparta.doblock.member.entity.MemberDetailsImpl;
import com.sparta.doblock.notification.dto.NotificationDto;
import com.sparta.doblock.notification.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmitterRepository emitterRepository;
    private static final long DEFAULT_TIMEOUT = 1000 * 60;

    public SseEmitter subscribe(MemberDetailsImpl memberDetails, String lastEventId) {

        String emitterId = createTimeIncludeId(memberDetails.getMember().getId());

        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteByEmitterId(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteByEmitterId(emitterId));

        String eventId = createTimeIncludeId(memberDetails.getMember().getId());
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId = " + memberDetails.getMember().getId() + "]");

        if (hasLostData(lastEventId)){
            sendLostData(emitter, memberDetails.getMember().getId(), emitterId, lastEventId);
        }

        return emitter;
    }

    public String createTimeIncludeId(Long memberId){
        return memberId + "_" + System.currentTimeMillis();
    }

    public void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data){

        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));

        } catch (IOException e){
            emitterRepository.deleteByEmitterId(emitterId);
        }
    }

    public boolean hasLostData(String lastEventId){
        return !lastEventId.isEmpty();
    }

    public void sendLostData(SseEmitter emitter, Long memberId, String emitterId, String lastEventId){

        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByMemberId(memberId);

        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0 )
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    @Async
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @TransactionalEventListener(classes = BadgeEvents.CreateBadgeEvent.class)
    public void send(BadgeEvents.CreateBadgeEvent badgeEvent){

        NotificationDto notificationDto = NotificationDto.builder()
                .title("뱃지를 획득했습니다.")
                .message("축하 게시글을 피드에 업로드할까요?")
                .badgeType(badgeEvent.getBadgeType())
                .badgeName(badgeEvent.getBadgeType().getBadgeName())
                .badgeImage(badgeEvent.getBadgeType().getBadgeImage())
                .build();

        String eventId = createTimeIncludeId(badgeEvent.getMember().getId());

        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(badgeEvent.getMember().getId());
        emitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notificationDto);
                    sendNotification(emitter, eventId, key, notificationDto);
                }
        );
    }
}
