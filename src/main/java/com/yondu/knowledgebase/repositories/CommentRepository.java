package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByParentCommentIdAndPageId(Long parentCommentId, Long page_id);
    List<Comment> findAllByParentCommentIdIsNullAndPageId(Long page_id);
}
