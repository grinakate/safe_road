package ru.itmo.saferoad.notifications.application.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.itmo.saferoad.notifications.domain.Notification;
import ru.itmo.saferoad.notifications.domain.repository.NotificationRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSseService {

    private final NotificationRepository notificationRepository;

    // map userId -> connection
    private final ConcurrentMap<Long, Connection> emitters = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r);
        t.setName("notification-sse-timeouter");
        t.setDaemon(true);
        return t;
    });

    private final ExecutorService sendExecutor = Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors()));

    // confirmation timeout will be read from properties
    private final ru.itmo.saferoad.notifications.config.NotificationsProperties notificationsProperties;

    public SseEmitter createEmitter(Long userId) {
        Objects.requireNonNull(userId, "userId");

        SseEmitter emitter = new SseEmitter(0L); // no built-in timeout
        Connection conn = new Connection(emitter);

        // register
        emitters.put(userId, conn);

        emitter.onCompletion(() -> {
            log.debug("Emitter completed for user {}", userId);
            emitters.remove(userId);
        });

        emitter.onTimeout(() -> {
            log.debug("Emitter timeout for user {}", userId);
            emitters.remove(userId);
        });

        emitter.onError((ex) -> {
            log.debug("Emitter error for user {}: {}", userId, ex.getMessage());
            emitters.remove(userId);
        });

        // schedule confirmation timeout
        long timeoutMs = notificationsProperties.getConfirmationTimeoutMs();
        scheduler.schedule(() -> {
            Connection c = emitters.get(userId);
            if (c != null && !c.confirmed.get()) {
                log.debug("Emitter for user {} not confirmed within {} ms, removing", userId, timeoutMs);
                try {
                    c.emitter.complete();
                } catch (Exception e) {
                    // ignore
                }
                emitters.remove(userId);
            }
        }, timeoutMs, TimeUnit.MILLISECONDS);

        return emitter;
    }

    public void confirmEmitter(Long userId) {
        Connection c = emitters.get(userId);
        if (c != null) {
            c.confirmed.set(true);
            log.debug("Emitter for user {} confirmed", userId);
        }
    }

    public void removeEmitter(Long userId) {
        Connection c = emitters.remove(userId);
        if (c != null) {
            try {
                c.emitter.complete();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    @Scheduled(fixedDelayString = "${notifications.scheduler-poll-interval-ms:5000}")
    public void pushPendingNotifications() {
        Set<Long> userIds = emitters.keySet();
        if (userIds.isEmpty()) {
            return;
        }

        List<Callable<Void>> tasks = new ArrayList<>();

        for (Long userId : userIds) {
            tasks.add(() -> {
                Connection c = emitters.get(userId);
                if (c == null) return null;

                List<Notification> list = notificationRepository.findByUserIdAndIsReadFalse(userId);
                if (list.isEmpty()) return null;

                for (Notification n : list) {
                    try {
                        // send notification as JSON string (content) along with title
                        Map<String, Object> payload = Map.of(
                                "id", n.getId(),
                                "title", n.getTitle(),
                                "content", n.getContent(),
                                "createdAt", n.getCreatedAt()
                        );
                        c.emitter.send(SseEmitter.event().name("notification").data(payload));
                        n.setIsRead(true);
                    } catch (Exception e) {
                        log.warn("Failed to send notification {} to user {}, removing emitter: {}", n.getId(), userId, e.getMessage());
                        removeEmitter(userId);
                        break;
                    }
                }

                try {
                    notificationRepository.saveAll(list);
                } catch (Exception e) {
                    log.error("Failed to mark notifications as read for user {}: {}", userId, e.getMessage());
                }

                return null;
            });
        }

        try {
            List<Future<Void>> futures = sendExecutor.invokeAll(tasks);
            for (Future<Void> f : futures) {
                try { f.get(); } catch (Exception ignored) {}
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
        sendExecutor.shutdownNow();
    }

    private static class Connection {
        final SseEmitter emitter;
        final AtomicBoolean confirmed = new AtomicBoolean(false);

        Connection(SseEmitter emitter) {
            this.emitter = emitter;
        }
    }
}



