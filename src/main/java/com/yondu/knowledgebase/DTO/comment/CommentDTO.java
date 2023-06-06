package com.yondu.knowledgebase.DTO.comment;

import java.time.LocalDateTime;

public class CommentDTO {

    private Long id;
    private LocalDateTime date;
    private String comment;
    private Long userId;

    public CommentDTO() {
    }

    public CommentDTO(Long id, LocalDateTime date, String comment, Long userId) {
        this.id = id;
        this.date = date;
        this.comment = comment;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
