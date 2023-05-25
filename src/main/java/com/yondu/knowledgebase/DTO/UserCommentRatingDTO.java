package com.yondu.knowledgebase.DTO;

public class UserCommentRatingDTO {
    private String rating;
    private Long userId;
    private Long commentId;
    private String message;
    private int totalRating;

    public UserCommentRatingDTO(){};

    public UserCommentRatingDTO(Long userId, Long commentId, String ratingValue, int totalCommentRating, String message) {
        this.userId = userId;
        this.commentId = commentId;
        this.rating = ratingValue;
        this.totalRating = totalCommentRating;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(int totalRating) {
        this.totalRating = totalRating;
    }
}
