package com.yondu.knowledgebase.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.yondu.knowledgebase.entities.UserPageRating;

@Repository
public interface UserPageRatingRepository extends JpaRepository<UserPageRating, Long>{
	
	List<UserPageRating> findAll();

	@Query(value = "SELECT * FROM user_page_rating WHERE page_id=:pageId AND user_id=:userId", nativeQuery = true)
    public UserPageRating findByPageIdAndUserId(Long pageId, Long userId);
	
	@Query(value = "SELECT * FROM user_page_rating WHERE page_id=:pageId AND is_active=1", nativeQuery = true)
    public List<UserPageRating> findByPageId(@Param("pageId") Long pageId);
	
	@Query(value = "SELECT * FROM user_page_rating WHERE user_id=:userId AND is_active=1", nativeQuery = true)
    public List<UserPageRating> findByUserId(@Param("userId") Long userId);
	
	@Query(value = "SELECT * FROM user_page_rating WHERE is_active=1", nativeQuery = true)
    public List<UserPageRating> findAllActive();

}