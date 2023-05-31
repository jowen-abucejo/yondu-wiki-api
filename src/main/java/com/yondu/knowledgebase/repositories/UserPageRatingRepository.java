package com.yondu.knowledgebase.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.yondu.knowledgebase.entities.UserPageRating;

@Repository
public interface UserPageRatingRepository extends JpaRepository<UserPageRating, Long>{

	@Query(value = "SELECT * FROM user_page_rating WHERE page_id=:pageId AND user_id=:userId", nativeQuery = true)
    public UserPageRating findByPageIdAndUserId(Long pageId, Long userId);

}