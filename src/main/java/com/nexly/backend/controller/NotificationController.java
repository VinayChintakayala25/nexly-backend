package com.nexly.backend.controller;

import com.nexly.backend.entity.Notification;
import com.nexly.backend.entity.User;
import com.nexly.backend.repository.UserRepository;
import com.nexly.backend.service.NotificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(
            NotificationService notificationService,
            UserRepository userRepository) {

        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    // ==========================================
    // GET CURRENT USER
    // ==========================================

    private User getCurrentUser(
            Authentication authentication) {

        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    // ==========================================
    // GET ALL NOTIFICATIONS
    // ==========================================

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(
            Authentication authentication) {

        User user = getCurrentUser(authentication);

        return ResponseEntity.ok(
                notificationService.getNotifications(user)
        );
    }

    // ==========================================
    // GET UNREAD NOTIFICATIONS
    // ==========================================

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>>
    getUnreadNotifications(
            Authentication authentication) {

        User user = getCurrentUser(authentication);

        return ResponseEntity.ok(
                notificationService
                        .getUnreadNotifications(user)
        );
    }

    // ==========================================
    // GET UNREAD COUNT
    // ==========================================

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(
            Authentication authentication) {

        User user = getCurrentUser(authentication);

        return ResponseEntity.ok(
                notificationService
                        .getUnreadCount(user)
        );
    }

    // ==========================================
    // MARK ONE AS READ
    // ==========================================

    @PutMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(
            @PathVariable Long id,
            Authentication authentication) {

        User user = getCurrentUser(authentication);

        notificationService.markAsRead(
                id,
                user
        );

        return ResponseEntity.ok(
                "Notification marked as read"
        );
    }

    // ==========================================
    // MARK ALL AS READ
    // ==========================================

    @PutMapping("/read-all")
    public ResponseEntity<String> markAllAsRead(
            Authentication authentication) {

        User user = getCurrentUser(authentication);

        notificationService.markAllAsRead(user);

        return ResponseEntity.ok(
                "All notifications marked as read"
        );
    }
}