package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
