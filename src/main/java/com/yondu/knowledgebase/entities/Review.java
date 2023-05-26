package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "page_version_id")
    private PageVersion pageVersion;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String comment;

    private LocalDate reviewDate;
    private String status;

    public Review(Long id, PageVersion pageVersion, User user, String comment, LocalDate reviewDate, String status) {
        this.id = id;
        this.pageVersion = pageVersion;
        this.user = user;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public PageVersion getPageVersion() {
        return pageVersion;
    }

    public User getUser() {
        return user;
    }

    public String getComment() {
        return comment;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", pageVersion=" + pageVersion +
                ", user=" + user +
                ", comment='" + comment + '\'' +
                ", reviewDate=" + reviewDate +
                ", status='" + status + '\'' +
                '}';
    }
}
