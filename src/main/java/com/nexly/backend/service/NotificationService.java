package com.nexly.backend.service;

import com.nexly.backend.entity.Notification;
import com.nexly.backend.entity.User;
import com.nexly.backend.repository.NotificationRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final WebSocketNotificationService webSocketNotificationService;

    public NotificationService(
            NotificationRepository notificationRepository,
            WebSocketNotificationService webSocketNotificationService) {

        this.notificationRepository = notificationRepository;
        this.webSocketNotificationService =
                webSocketNotificationService;
    }

    // =========================================================
    // CREATE NOTIFICATION
    // =========================================================

    public Notification createNotification(
            User user,
            String message,
            String type) {

        Notification notification = new Notification();

        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);

        Notification savedNotification =
                notificationRepository.save(notification);

        webSocketNotificationService.sendToUser(
                user.getId(),
                savedNotification
        );

        return savedNotification;
    }

    // =========================================================
    // GET ALL NOTIFICATIONS
    // =========================================================

    public List<Notification> getNotifications(
            User user) {

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user);
    }

    // =========================================================
    // GET UNREAD NOTIFICATIONS
    // =========================================================

    public List<Notification> getUnreadNotifications(
            User user) {

        return notificationRepository
                .findByUserAndReadFalseOrderByCreatedAtDesc(user);
    }

    // =========================================================
    // GET UNREAD COUNT
    // =========================================================

    public long getUnreadCount(
            User user) {

        return notificationRepository
                .countByUserAndReadFalse(user);
    }

    // =========================================================
    // MARK ONE NOTIFICATION AS READ
    // =========================================================

    public void markAsRead(
            Long notificationId,
            User user) {

        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"
                                )
                        );

        if (notification.getUser() == null ||
                notification.getUser().getId() == null ||
                !notification.getUser()
                        .getId()
                        .equals(user.getId())) {

            throw new RuntimeException(
                    "You are not allowed to modify this notification"
            );
        }

        notification.setRead(true);

        notificationRepository.save(notification);
    }

    // =========================================================
    // MARK ALL NOTIFICATIONS AS READ
    // =========================================================

    public void markAllAsRead(
            User user) {

        List<Notification> notifications =
                notificationRepository
                        .findByUserAndReadFalseOrderByCreatedAtDesc(
                                user
                        );

        for (Notification notification : notifications) {
            notification.setRead(true);
        }

        notificationRepository.saveAll(notifications);
    }
}