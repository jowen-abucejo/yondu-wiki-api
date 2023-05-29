package com.yondu.knowledgebase.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    public Review() {
    }

    public Review(PageVersion pageVersion, User user, String comment, LocalDate reviewDate, String status) {
        this.pageVersion = pageVersion;
        this.user = user;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    @JsonBackReference
    public PageVersion getPageVersion() {
        return pageVersion;
    }

    @JsonBackReference
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

    public void setPageVersion(PageVersion pageVersion) {
        this.pageVersion = pageVersion;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
