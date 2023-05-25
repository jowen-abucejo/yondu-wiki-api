package com.yondu.knowledgebase.DTO;

import java.time.LocalDateTime;

public class CommentReqDTO {
    private Long userId;
    private LocalDateTime date;
    private String comment;
    private Long pageId;
    private Long ratingId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public Long getRatingId() {
        return ratingId;
    }
    public void setRatingId(Long ratingId) {
        this.ratingId = ratingId;
    }
}
