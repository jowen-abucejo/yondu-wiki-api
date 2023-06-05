package com.yondu.knowledgebase.DTO.comment;

public class CommentCountResponseDTO {
    private Long entityId;
    private String entityType;
    private Long totalCommentCount;

    public CommentCountResponseDTO() {
    }

    public CommentCountResponseDTO(Long entityId, String entityType, Long totalCommentCount) {
        this.entityId = entityId;
        this.entityType = entityType;
        this.totalCommentCount = totalCommentCount;
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

    public Long getTotalCommentCount() {
        return totalCommentCount;
    }

    public void setTotalCommentCount(Long totalCommentCount) {
        this.totalCommentCount = totalCommentCount;
    }
}
