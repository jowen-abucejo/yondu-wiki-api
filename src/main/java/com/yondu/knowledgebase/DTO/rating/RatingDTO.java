package com.yondu.knowledgebase.DTO.rating;

public class RatingDTO {
	private Long user_id;
	private Long entity_id;
	private String entity_type;
	private String rating;
	
	public RatingDTO() {}
	
	public RatingDTO(Long user_id, Long entity_id, String entity_type, String rating) {
		this.user_id = user_id;
		this.entity_id = entity_id;
		this.entity_type = entity_type;
		this.rating = rating;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public Long getEntity_id() {
		return entity_id;
	}

	public void setEntity_id(Long entity_id) {
		this.entity_id = entity_id;
	}

	public String getEntity_type() {
		return entity_type;
	}

	public void setEntity_type(String entity_type) {
		this.entity_type = entity_type;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}
}
