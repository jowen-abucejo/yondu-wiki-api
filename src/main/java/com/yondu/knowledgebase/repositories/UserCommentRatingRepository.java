package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.UserCommentRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface UserCommentRatingRepository extends JpaRepository <UserCommentRating, Long> {
    UserCommentRating findByUserIdAndCommentId(Long userId, Long commentId);

    List<UserCommentRating> findByCommentId(Long commentId);
}
