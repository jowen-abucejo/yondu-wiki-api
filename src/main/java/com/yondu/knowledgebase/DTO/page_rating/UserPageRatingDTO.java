package com.yondu.knowledgebase.DTO.page_rating;

public class UserPageRatingDTO {
	private String rating;
	private Long userId;
	private Long pageId;
	
	public UserPageRatingDTO() {}
	
	public UserPageRatingDTO(String rating, Long userId, Long pageId) {
		this.rating = rating;
		this.userId = userId;
		this.pageId = pageId;
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

	public Long getPageId() {
		return pageId;
	}

	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}
}