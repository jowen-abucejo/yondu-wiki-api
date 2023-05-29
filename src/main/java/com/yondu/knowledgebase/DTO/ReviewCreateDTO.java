package com.yondu.knowledgebase.DTO;

public class ReviewCreateDTO {

    private Long pageVersionId;
    private String comment;
    private String status;

    // Constructors, getters, and setters

    public ReviewCreateDTO() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
