package com.yondu.knowledgebase.DTO;

public class ReviewCreateDTO {

    private String comment;
    private String status;


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
