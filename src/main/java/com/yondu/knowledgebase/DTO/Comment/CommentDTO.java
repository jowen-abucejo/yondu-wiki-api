package com.yondu.knowledgebase.DTO.Comment;

public class CommentDTO {

    private String comment;
    private Long pageId;
    private Long userId;
    private Long commentParentId;
    private int totalCommentRating;


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCommentParentId() {
        return commentParentId;
    }

    public void setCommentParentId(Long commentParentId) {
        this.commentParentId = commentParentId;
    }

    public int getTotalCommentRating() {
        return totalCommentRating;
    }

    public void setTotalCommentRating(int totalCommentRating) {
        this.totalCommentRating = totalCommentRating;
    }


    public CommentDTO(String comment, Long pageId, Long userId, Long commentParentId, int totalCommentRating) {
        this.comment = comment;
        this.pageId = pageId;
        this.userId = userId;
        this.commentParentId = commentParentId;
        this.totalCommentRating = totalCommentRating;
    }
    public CommentDTO() {
    }

}
