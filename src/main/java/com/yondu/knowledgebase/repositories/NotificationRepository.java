package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Notification;
import com.yondu.knowledgebase.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM notifications n WHERE user = ?1 ORDER BY timestamp")
    Page<Notification> getNotificationsByUser(User user, Pageable pageable);
}
