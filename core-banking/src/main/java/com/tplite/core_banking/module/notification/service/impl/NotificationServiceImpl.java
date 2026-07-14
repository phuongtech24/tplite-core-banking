package com.tplite.core_banking.module.notification.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tplite.core_banking.common.exception.ResourceNotFoundException;
import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.module.notification.dto.NotificationResponse;
import com.tplite.core_banking.module.notification.entity.Notification;
import com.tplite.core_banking.module.notification.entity.NotificationChannel;
import com.tplite.core_banking.module.notification.entity.NotificationStatus;
import com.tplite.core_banking.module.notification.entity.NotificationType;
import com.tplite.core_banking.module.notification.repository.NotificationRepository;
import com.tplite.core_banking.module.notification.service.NotificationService;
import com.tplite.core_banking.module.user.entity.User;
import com.tplite.core_banking.module.user.repository.UserRepository;

@Service
public class NotificationServiceImpl implements NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public NotificationResponse createInAppNotification(User user, String title, String content, NotificationType type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setNotificationType(type);
        notification.setChannel(NotificationChannel.IN_APP);
        notification.setStatus(NotificationStatus.SENT);

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification created: notificationId={}, userId={}, type={}", savedNotification.getId(), user.getId(), type);
        return NotificationResponse.from(savedNotification);
    }

    @Override
    public PageResponse<NotificationResponse> getMyNotifications(String email, NotificationStatus status, Pageable pageable) {
        User user = findUserByEmail(email);
        Page<NotificationResponse> notifications = status == null
                ? notificationRepository.findByUser(user, pageable).map(NotificationResponse::from)
                : notificationRepository.findByUserAndStatus(user, status, pageable).map(NotificationResponse::from);
        return PageResponse.from(notifications);
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(String email, UUID notificationId) {
        User user = findUserByEmail(email);
        Notification notification = notificationRepository.findByIdAndUser(notificationId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        notification.setStatus(NotificationStatus.READ);
        notification.setReadAt(LocalDateTime.now());
        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification marked as read: notificationId={}, userId={}", savedNotification.getId(), user.getId());
        return NotificationResponse.from(savedNotification);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
