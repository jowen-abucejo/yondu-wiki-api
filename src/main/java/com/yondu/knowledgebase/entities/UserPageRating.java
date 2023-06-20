package com.yondu.knowledgebase.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class UserPageRating {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, length = 5)
	private String rating;
	@Column(name = "is_active")
    private Boolean active;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="page_id", nullable=false, referencedColumnName="id")
	private Page page;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id", nullable=false, referencedColumnName="id")
	private User user;
	public UserPageRating() {}
	
	public UserPageRating(Long id, String rating, Boolean active, Page page, User user) {
		this.id = id;
		this.rating = rating;
		this.active = active;
		this.page = page;
		this.user = user;
	}

	public Long getId() {
		return id;
	}
	public String getRating() {
		return rating;
	}
	
	public Page getPage() {
		return page;
	}
	
	public User getUser() {
		return user;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public void setUser(User user) {
		this.user = user;
	}
}