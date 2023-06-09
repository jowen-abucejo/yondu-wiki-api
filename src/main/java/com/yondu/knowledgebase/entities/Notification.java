package com.yondu.knowledgebase.entities;

import com.yondu.knowledgebase.enums.NotificationType;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity(name="notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    @Column(nullable = false)
    private String message;
    private LocalDateTime timestamp;
    private boolean isRead;
    private String notificationType;
    private String type;
    private long typeId;

    public Notification() {
    }

    public Notification(long id, User user, String message, LocalDateTime timestamp, boolean isRead) {
        this.id = id;
        this.user = user;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.type = NotificationType.GENERAL.getCode();
        this.typeId = -1;
    }

    public Notification(User user, String message, String notificationType, String type, long typeId) {
        this.user = user;
        this.message = message;
        this.notificationType = notificationType;
        this.type = type;
        this.typeId = typeId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }
}
