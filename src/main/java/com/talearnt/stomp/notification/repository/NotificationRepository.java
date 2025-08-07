package com.talearnt.stomp.notification.repository;

import com.talearnt.stomp.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
}
