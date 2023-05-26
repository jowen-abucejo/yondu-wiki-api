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
	
	@Column(nullable = false, length = 3)
	private Integer rating;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="page_id", nullable=false, referencedColumnName="id")
	private Page page;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id", nullable=false, referencedColumnName="id")
	private User user;
	
	public UserPageRating() {}

	public UserPageRating(Long id, Integer rating, Page page, User user) {
		this.id = id;
		this.rating = rating;
		this.page = page;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public Integer getRating() {
		return rating;
	}

	public Page getPage() {
		return page;
	}

	public User getUser() {
		return user;
	}
}
