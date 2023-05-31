package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.UserCommentRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface UserCommentRatingRepository extends JpaRepository <UserCommentRating, Long> {
    UserCommentRating findByUserIdAndCommentId(Long userId, Long commentId);

    @Query("SELECT COUNT(*) FROM UserCommentRating WHERE comment.id = :commentId AND rating = 'UP'")
    int totalCommentRating (@Param("commentId") Long commentId);
}
