package com.yondu.knowledgebase.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yondu.knowledgebase.DTO.comment.CommentDTO;
import com.yondu.knowledgebase.DTO.user.UserDTO;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    private LocalDateTime dateCreated;

    private String comment;

    private Long parentCommentId;

    private Long entityId;

    private String entityType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "comment_mentions", joinColumns = @JoinColumn(name = "comment_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> commentMentions = new HashSet<>();

    //Replies are turned on by default
    private boolean allowReply = true;

    public Comment() {
    }

    /**
     * @param id
     * @param dateCreated
     * @param comment
     * @param parentCommentId
     * @param entityId
     * @param entityType
     * @param user
     * @param commentMentions
     */
    public Comment(Long id, LocalDateTime dateCreated, String comment, Long parentCommentId, Long entityId, String entityType, User user, Set<User> commentMentions, boolean allowReply) {
        this.id = id;
        this.dateCreated = dateCreated;
        this.comment = comment;
        this.parentCommentId = parentCommentId;
        this.entityId = entityId;
        this.entityType = entityType;
        this.user = user;
        this.commentMentions = commentMentions;
        this.allowReply = allowReply;
    }

    public Comment(LocalDateTime dateCreated, String comment, Long entityId, String entityType, User user) {
        this.dateCreated = dateCreated;
        this.comment = comment;
        this.entityId = entityId;
        this.entityType = entityType;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<User> getCommentMentions() {
        return commentMentions;
    }

    public void setCommentMentions(Set<User> commentMentions) {
        this.commentMentions = commentMentions;
    }

    public boolean isAllowReply() {
        return allowReply;
    }

    public void setAllowReply(boolean allowReply) {
        this.allowReply = allowReply;
    }
}
