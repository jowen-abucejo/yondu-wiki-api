package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query (value = "SELECT c FROM Comment c WHERE c.entityType = :entityType AND c.entityId = :entityId AND c.parentCommentId = :parentCommentId AND c.isDeleted = false")
    List<Comment> findAllCommentReplies (String entityType, Long entityId, Long parentCommentId);

    @Query (value = "SELECT c FROM Comment c WHERE c.entityType = :entity AND c.entityId = :id AND c.isDeleted = false")
    List<Comment> getAllComments(String entity, Long id);

    @Query (value = "SELECT c FROM Comment c WHERE c.entityType = :entity AND c.entityId = :id AND c.isDeleted = false AND parentCommentId = null")
    List<Comment> getAllParentComments(String entity, Long id);

    @Query (value = "SELECT COUNT (c) FROM Comment c WHERE c.entityType = :entity AND c.entityId = :id AND c.isDeleted = false")
    Long countByEntityTypeAndEntityId(String entity, Long id);

}
