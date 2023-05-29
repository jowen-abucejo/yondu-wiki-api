package com.yondu.knowledgebase.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_page_permission")
public class UserPagePermission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permission permission;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "page_id")
    private Page page;

    private Boolean isActive;

    @CreatedDate
    private LocalDateTime dateCreated;

    @LastModifiedDate
    private LocalDateTime lastModified;

    public UserPagePermission() {
    }

    public UserPagePermission(Long id, Permission permission, User user, Page page, Boolean isActive, LocalDateTime dateCreated, LocalDateTime lastModified) {
        this.id = id;
        this.permission = permission;
        this.user = user;
        this.page = page;
        this.isActive = isActive;
        this.dateCreated = dateCreated;
        this.lastModified = lastModified;
    }

    public UserPagePermission(Permission permission, User user, Page page, Boolean isActive, LocalDateTime dateCreated, LocalDateTime lastModified) {
        this.permission = permission;
        this.user = user;
        this.page = page;
        this.isActive = isActive;
        this.dateCreated = dateCreated;
        this.lastModified = lastModified;
    }

    public Long getId() {
        return id;
    }

    public Permission getPermission() {
        return permission;
    }

    public User getUser() {
        return user;
    }

    public Page getPage() {
        return page;
    }

    public Boolean getActive() {
        return isActive;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    @Override
    public String toString() {
        return "UserPagePermission{" +
                "id=" + id +
                ", permission=" + permission +
                ", user=" + user +
                ", page=" + page +
                ", isActive=" + isActive +
                ", dateCreated=" + dateCreated +
                ", lastModified=" + lastModified +
                '}';
    }
}
