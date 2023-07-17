package com.yondu.knowledgebase.DTO.rating;

public class TotalVoteDTO {
	private Long entity_id;
	private String entity_type;
	private int upvote;
	private int downvote;
	private int total_vote;
	
	public TotalVoteDTO() {}
	
	public TotalVoteDTO(Long entity_id, String entity_type, int upvote, int downvote, int total_vote) {
		this.entity_id = entity_id;
		this.entity_type = entity_type;
		this.upvote = upvote;
		this.downvote = downvote;
		this.total_vote = total_vote;
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

	public int getUpvote() {
		return upvote;
	}

	public void setUpvote(int upvote) {
		this.upvote = upvote;
	}

	public int getDownvote() {
		return downvote;
	}

	public void setDownvote(int downvote) {
		this.downvote = downvote;
	}

	public int getTotal_vote() {
		return total_vote;
	}

	public void setTotal_vote(int total_vote) {
		this.total_vote = total_vote;
	}
}
