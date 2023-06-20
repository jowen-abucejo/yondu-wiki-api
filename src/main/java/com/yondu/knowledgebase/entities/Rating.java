package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

@Entity
public class Rating {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id", nullable=false, referencedColumnName="id")
	private User user;
	
	@Column(nullable = false)
	private Long entity_id;


	@Pattern(regexp="^(Page|Comment|Post)$",message="This field can only accept 'Page', 'Comment' and 'Post'")
	@Column(nullable = false)
	private String entity_type;
	@Pattern(regexp="^(UP|DOWN)$", message="This field can only accept 'UP' or 'DOWN'")
	@Column(nullable = false)
	private String rating;
	@Column(name = "is_active")
    private Boolean active = true;
	
	public Rating() {}

	public Rating(Long id, User user, Long entity_id, String entity_type, String rating, Boolean active) {
		this.id = id;
		this.user = user;
		this.entity_id = entity_id;
		this.entity_type = entity_type;
		this.rating = rating;
		this.active = active;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}
