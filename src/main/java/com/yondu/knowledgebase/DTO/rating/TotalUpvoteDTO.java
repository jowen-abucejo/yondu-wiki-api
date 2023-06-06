package com.yondu.knowledgebase.DTO.rating;

public class TotalUpvoteDTO {
	private Long entity_id;
	private String entity_type;
	private int total_upvote;
	
	public TotalUpvoteDTO() {}
	
	public TotalUpvoteDTO(Long entity_id, String entity_type, int total_upvote) {
		this.entity_id = entity_id;
		this.entity_type = entity_type;
		this.total_upvote = total_upvote;
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

	public int getTotal_upvote() {
		return total_upvote;
	}

	public void setTotal_upvote(int total_upvote) {
		this.total_upvote = total_upvote;
	}
}
