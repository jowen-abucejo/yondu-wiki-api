package com.yondu.knowledgebase.DTO.comment;

import com.fasterxml.jackson.annotation.JsonInclude;

public class CommentRequestDTO {
    private String comment;
    private Long userId;
    private Long entityId;
    private String entityType;

    public CommentRequestDTO() {
    }

    public CommentRequestDTO(String comment, Long userId, Long entityId, String entityType) {
        this.comment = comment;
        this.userId = userId;
        this.entityId = entityId;
        this.entityType = entityType;
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

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

}
