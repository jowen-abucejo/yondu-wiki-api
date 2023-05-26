package com.yondu.knowledgebase.DTO.Comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yondu.knowledgebase.entities.Comment;
import com.yondu.knowledgebase.entities.User;

import java.time.LocalDateTime;

public class CommentRequestDTO {
    private Long id;
    private LocalDateTime date;
    private String comment;
    private Long pageId;
    private Long ratingId;
    private User user;

    // Getter and Setter
    public LocalDateTime getDate() { return date;}
    public void setDate(LocalDateTime date) { this.date = date; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Long getPageId() { return pageId; }
    public void setPageId(Long pageId) { this.pageId = pageId; }
    public Long getRatingId() { return ratingId; }
    public void setRatingId(Long ratingId) { this.ratingId = ratingId; }
    public User getUser() { return user;}
    public void setUser(User user) { this.user = user;}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // Constructor
    public CommentRequestDTO() {}

    public CommentRequestDTO(Comment comment) {
        this.id = comment.getId();
        this.date = comment.getDateCreated();
        this.comment = comment.getComment();
        this.user = comment.getUser();
    }
}
