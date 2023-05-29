package com.yondu.knowledgebase.DTO;

public class UserCommentRatingDTO {
    private String rating;
    private Long userId;
    private Long commentId;
    private int totalCommentRating;

    public UserCommentRatingDTO(){};

    public UserCommentRatingDTO(Long userId, Long commentId, String ratingValue, int totalCommentRating) {
        this.userId = userId;
        this.commentId = commentId;
        this.rating = ratingValue;
        this.totalCommentRating = totalCommentRating;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public int getTotalCommentRating() {
        return totalCommentRating;
    }

    public void setTotalCommentRating(int totalCommentRating) {
        this.totalCommentRating = totalCommentRating;
    }
}
