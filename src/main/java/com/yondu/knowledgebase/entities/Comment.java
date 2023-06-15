package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.annotation.CreatedBy;
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
    @Column(updatable = false)
    private LocalDateTime dateCreated;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private Long parentCommentId;

    @Column(nullable = false)
    private Long entityId;

    @Pattern(regexp="^(PAGE|POST)$",message="This field can only accept 'PAGE' and 'POST'")
    @Column(nullable = false)
    private String entityType;

    @CreatedBy
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "comment_mentions", joinColumns = @JoinColumn(name = "comment_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> commentMentions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "comment_replies", joinColumns = @JoinColumn(name = "comment_id"), inverseJoinColumns = @JoinColumn(name = "reply_id"))
    private Set<Comment> commentReplies = new HashSet<>();

    @Column(name = "allow_reply", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean allowReply = true;

    @Column(name = "is_Deleted", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted = false;

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
     * @param isDeleted
     */
    public Comment(Long id, LocalDateTime dateCreated, String comment, Long parentCommentId, Long entityId, String entityType, User user, Set<User> commentMentions, Set<Comment> commentReplies, boolean allowReply, boolean isDeleted) {
        this.id = id;
        this.dateCreated = dateCreated;
        this.comment = comment;
        this.parentCommentId = parentCommentId;
        this.entityId = entityId;
        this.entityType = entityType;
        this.user = user;
        this.commentMentions = commentMentions;
        this.commentReplies = commentReplies;
        this.allowReply = allowReply;
        this.isDeleted = isDeleted;
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

    public Set<Comment> getCommentReplies() {
        return commentReplies;
    }

    public void setCommentReplies(Set<Comment> commentReplies) {
        this.commentReplies = commentReplies;
    }

    public boolean isAllowReply() {
        return allowReply;
    }

    public void setAllowReply(boolean allowReply) {
        this.allowReply = allowReply;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
