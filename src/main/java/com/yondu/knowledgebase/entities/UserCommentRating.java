package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

@Entity
public class UserCommentRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", referencedColumnName = "id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public UserCommentRating() {
    }

    /**
     * @param id
     * @param rating
     * @param comment
     * @param user
     */
    public UserCommentRating(Long id, int rating, Comment comment, User user) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.user = user;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the rating
     */
    public int getRating() {
        return rating;
    }

    /**
     * @return the comment
     */
    public Comment getComment() {
        return comment;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @set the rating
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * @set the comment
     */
    public void setComment(Comment comment) {
        this.comment = comment;
    }

    /**
     * @set the user
     */
    public void setUser(User user) {
        this.user = user;
    }
}
