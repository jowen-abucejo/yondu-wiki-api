package com.yondu.knowledgebase.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(name = "workflow_step_id")
    private WorkflowStep workflowStep;

    private String comment;
    private LocalDateTime reviewDate;

    private String status;

    public Review() {
    }

    public Review(PageVersion pageVersion, User user,WorkflowStep workflowStep, String comment, LocalDateTime reviewDate, String status) {
        this.pageVersion = pageVersion;
        this.user = user;
        this.workflowStep = workflowStep;
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

    @JsonBackReference
    public WorkflowStep getWorkflowStep() {
        return workflowStep;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getReviewDate() {
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

    public void setWorkflowStep(WorkflowStep workflowStep) {
        this.workflowStep = workflowStep;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
