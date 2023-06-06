package com.yondu.knowledgebase.DTO.comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yondu.knowledgebase.DTO.UserDTO;
import com.yondu.knowledgebase.entities.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class CommentResponseDTO {
    private Long id;
    private LocalDateTime date;
    private String comment;
    private Long userId;
    private Long entityId;
    private String entityType;
    private Long totalReplies;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<MentionedUserResponseDTO> commentMentions;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List <CommentDTO> replies;

    public CommentResponseDTO() {
    }

    public CommentResponseDTO(Long id, LocalDateTime date, String comment, Long userId, Long entityId, String entityType, Set<MentionedUserResponseDTO> commentMentions, List<CommentDTO> replies, Long totalReplies) {
        this.id = id;
        this.date = date;
        this.comment = comment;
        this.userId = userId;
        this.entityId = entityId;
        this.entityType = entityType;
        this.totalReplies = totalReplies;
        this.commentMentions = commentMentions;
        this.replies = replies;
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

    public Long getTotalReplies() {
        return totalReplies;
    }

    public void setTotalReplies(Long totalReplies) {
        this.totalReplies = totalReplies;
    }

    public Set<MentionedUserResponseDTO> getCommentMentions() {
        return commentMentions;
    }

    public void setCommentMentions(Set<MentionedUserResponseDTO> commentMentions) {
        this.commentMentions = commentMentions;
    }

    public List<CommentDTO> getReplies() {
        return replies;
    }

    public void setReplies(List<CommentDTO> replies) {
        this.replies = replies;
    }
}

