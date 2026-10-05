package com.nexly.backend.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationService(
            SimpMessagingTemplate messagingTemplate) {

        this.messagingTemplate = messagingTemplate;
    }

    public void sendToUser(
            Long userId,
            Object notification) {

        messagingTemplate.convertAndSend(
                "/topic/user/" + userId,
                notification
        );
    }

    public void sendToAll(
            Object notification) {

        messagingTemplate.convertAndSend(
                "/topic/notifications",
                notification
        );
    }
}