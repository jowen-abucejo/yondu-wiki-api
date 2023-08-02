package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Notification;
import com.yondu.knowledgebase.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM notifications n WHERE user = ?1 ORDER BY timestamp DESC")
    Page<Notification> getNotificationsByUser(User user, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE notifications SET isRead = true WHERE user = ?1 AND isRead = false")
    void readAllNotification(User user);


    @Query("SELECT COUNT (n) FROM notifications n WHERE user = ?1 AND (isRead = false AND fromUser <> ?1 OR (isRead = false AND notificationType = 'APPROVAL' AND fromUser = ?1)) ")
    Long totalUnreadNotification(User user);

    @Query("SELECT n FROM notifications n WHERE user = ?1 AND (isRead = false AND fromUser <> ?1 OR (isRead = false AND notificationType = 'APPROVAL' AND fromUser = ?1)) ")
    Page<Notification> getUnreadNotification(User user, Pageable pageable);
}
