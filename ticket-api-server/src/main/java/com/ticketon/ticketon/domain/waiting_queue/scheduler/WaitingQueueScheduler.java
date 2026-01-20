//package com.ticketon.ticketon.domain.waiting_queue.scheduler;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.Duration;
//import java.util.Set;
//
//import static com.ticket.utils.RedisKeyConstants.ALLOWED_PREFIX;
//import static com.ticket.utils.RedisKeyConstants.WAITING_LINE;
//import static com.ticket.utils.StompConstants.TOPIC_ALLOWED;
//
//
//@Component
//public class WaitingQueueScheduler {
//
//    private final RedisTemplate<String, String> waitingRedisTemplate;
//    private final RedisTemplate<String, String> allowedRedisTemplate;
//    private final SimpMessagingTemplate messagingTemplate;
//
//    public WaitingQueueScheduler(
//            @Qualifier("waitingRedisTemplate") RedisTemplate<String, String> waitingRedisTemplate,
//            @Qualifier("reservationRedisTemplate") RedisTemplate<String, String> allowedRedisTemplate,
//            SimpMessagingTemplate messagingTemplate
//    ) {
//        this.waitingRedisTemplate = waitingRedisTemplate;
//        this.allowedRedisTemplate = allowedRedisTemplate;
//        this.messagingTemplate = messagingTemplate;
//    }
//
//    @Scheduled(fixedDelay = 1000)
//    public void checkAndNotify() {
//        String email = findNextInQueue();
//        if (email == null) {
//            return;
//        }
//
//        boolean removed = removeFromQueue(email);
//        if (!removed) {
//            return;
//        }
//        allowAccess(email);
//        notifyUser(email);
//    }
//
//    private String findNextInQueue() {
//        Set<String> emails = waitingRedisTemplate.opsForZSet().range(WAITING_LINE, 0, 0);
//        if (emails == null || emails.isEmpty()) {
//            return null;
//        }
//        return emails.iterator().next();
//    }
//
//    private boolean removeFromQueue(String email) {
//        Long removedCount = waitingRedisTemplate.opsForZSet().remove(WAITING_LINE, email);
//        return removedCount != null && removedCount > 0;
//    }
//
//    private void allowAccess(String email) {
//        allowedRedisTemplate.opsForValue().set(ALLOWED_PREFIX + email, "true", Duration.ofMinutes(2));
//    }
//
//    private void notifyUser(String email) {
//        messagingTemplate.convertAndSendToUser(email, TOPIC_ALLOWED, email);
//    }
//}