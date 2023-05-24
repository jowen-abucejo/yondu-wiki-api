package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rating_id")
    private UserCommentRating userCommentRating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id")
    private Page page;

    private LocalDateTime date;
    private Long parentCommentId;

    public Comment() {
    }

    public Comment(Long id, User user, UserCommentRating userCommentRating, Page page, LocalDateTime date, Long parentCommentId) {
        this.id = id;
        this.user = user;
        this.userCommentRating = userCommentRating;
        this.page = page;
        this.date = date;
        this.parentCommentId = parentCommentId;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public UserCommentRating getUserCommentRating() {
        return userCommentRating;
    }

    public Page getPage() {
        return page;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }
}
