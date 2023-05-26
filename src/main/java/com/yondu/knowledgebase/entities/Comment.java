package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    private LocalDateTime dateCreated;

    private String comment;

    private Long parentCommentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")

    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", referencedColumnName = "id")
    private Page page;

    private int totalCommentRating;

    public Comment() {
    }

    /**
     * @param id
     * @param dateCreated
     * @param comment
     * @param parentCommentId
     * @param user
     * @param page
     */
    public Comment(Long id, LocalDateTime dateCreated, String comment, Long parentCommentId, User user, Page page, int totalCommentRating) {
        this.id = id;
        this.dateCreated = dateCreated;
        this.comment = comment;
        this.parentCommentId = parentCommentId;
        this.user = user;
        this.page = page;
        this.totalCommentRating = totalCommentRating;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the dateCreated
     */
    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @return the parentCommentId
     */
    public Long getParentCommentId() {
        return parentCommentId;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @return the page
     */
    public Page getPage() {
        return page;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public int getTotalCommentRating() {
        return totalCommentRating;
    }

    public void setTotalCommentRating(int totalCommentRating) {
        this.totalCommentRating = totalCommentRating;
    }
}
