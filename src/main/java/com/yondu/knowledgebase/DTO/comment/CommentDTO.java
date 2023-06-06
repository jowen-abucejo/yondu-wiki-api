package com.yondu.knowledgebase.DTO.comment;

import com.yondu.knowledgebase.entities.User;

import java.time.LocalDateTime;
import java.util.Set;

public class CommentDTO {

    private Long id;
    private LocalDateTime date;
    private String comment;
    private Long userId;
    private Set <MentionedUserResponseDTO> commentMentions;

    public CommentDTO() {
    }

    public CommentDTO(Long id, LocalDateTime date, String comment, Long userId, Set<MentionedUserResponseDTO> commentMentions) {
        this.id = id;
        this.date = date;
        this.comment = comment;
        this.userId = userId;
        this.commentMentions = commentMentions;
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

    public Set<MentionedUserResponseDTO> getCommentMentions() {
        return commentMentions;
    }

    public void setCommentMentions(Set<MentionedUserResponseDTO> commentMentions) {
        this.commentMentions = commentMentions;
    }
}
