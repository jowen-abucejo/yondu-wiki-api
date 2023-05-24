package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

@Entity
public class UserCommentRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id")
    private Page page;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private int rating;

    // Constructors, getters, and setters

    public UserCommentRating() {
    }

    public UserCommentRating(Page page, User user, int rating) {
        this.page = page;
        this.user = user;
        this.rating = rating;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
