package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.Comment.CommentRequestDTO;
import com.yondu.knowledgebase.DTO.Comment.CommentResponseDTO;
import com.yondu.knowledgebase.entities.Comment;
import org.springframework.http.ResponseEntity;


public interface CommentService {
    public Comment createComment(CommentRequestDTO comment, Long userId, Long commentParentId);
    public CommentResponseDTO getAllComments();
    public int getTotalComments();
    Comment getComment(Long commentId);
}
