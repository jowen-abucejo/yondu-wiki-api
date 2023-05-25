package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    private LocalDateTime dateCreated;

    private String comment;

    private Long parentCommentId;

    @OneToMany(mappedBy = "comment")
    private List<UserCommentRating> userCommentRating = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", referencedColumnName = "id")
    private Page page;

    public Comment() {
    }

    /**
     * @param id
     * @param dateCreated
     * @param comment
     * @param parentCommentId
     * @param userCommentRating
     * @param user
     * @param page
     */
    public Comment(Long id, LocalDateTime dateCreated, String comment, Long parentCommentId, List<UserCommentRating> userCommentRating, User user, Page page) {
        this.id = id;
        this.dateCreated = dateCreated;
        this.comment = comment;
        this.parentCommentId = parentCommentId;
        this.userCommentRating = userCommentRating;
        this.user = user;
        this.page = page;
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
     * @return the userCommentRating
     */
    public List<UserCommentRating> getUserCommentRating() {
        return userCommentRating;
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
}
