package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query (value = "SELECT c FROM Comment c WHERE c.entityType = :entityType AND c.entityId = :entityId AND c.parentCommentId = :parentCommentId ")
    List<Comment> findAllCommentReplies (String entityType, Long entityId, Long parentCommentId);

    @Query (value = "SELECT COUNT (c) FROM Comment c WHERE c.parentCommentId = :parentCommentId")
    Long countAllReplies (Long parentCommentId);

    List<Comment> findByEntityTypeAndEntityId(String entity, Long id);

    Long countByEntityTypeAndEntityId(String entity, Long id);
}
