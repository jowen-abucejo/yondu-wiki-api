package com.yondu.knowledgebase.DTO.Comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yondu.knowledgebase.entities.Comment;
import com.yondu.knowledgebase.entities.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentRequestDTO {
    private Long id;
    private LocalDateTime date;
    private String comment;
    private Long pageId;
    private User user;

    private int totalCommentRating;

    private List<CommentRequestDTO> commentParentList;

    public List<CommentRequestDTO> getCommentParentList() {
        return commentParentList;
    }
    public void addCommentParent(CommentRequestDTO commentParent) {
        this.commentParentList.add(commentParent);
    }

    public void setCommentParentList(List<CommentRequestDTO> commentParentList) {
        this.commentParentList = commentParentList;
    }

    // Getter and Setter
    public LocalDateTime getDate() { return date;}
    public void setDate(LocalDateTime date) { this.date = date; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Long getPageId() { return pageId; }
    public void setPageId(Long pageId) { this.pageId = pageId; }

    public User getUser() { return user;}
    public void setUser(User user) { this.user = user;}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getTotalCommentRating() {
        return totalCommentRating;
    }

    public void setTotalCommentRating(int totalCommentRating) {
        this.totalCommentRating = totalCommentRating;
    }

    // Constructor
    public CommentRequestDTO() {
        this.commentParentList = new ArrayList<>();
    }

    public CommentRequestDTO(Comment comment) {
        this.id = comment.getId();
        this.date = comment.getDateCreated();
        this.comment = comment.getComment();
        this.user = comment.getUser();
        this.totalCommentRating = comment.getTotalCommentRating();
    }

}
