package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.Comment.CommentRequestDTO;
import com.yondu.knowledgebase.DTO.Comment.CommentResponseDTO;
import com.yondu.knowledgebase.entities.Comment;


public interface CommentService {
    public Comment createComment(CommentRequestDTO comment, Long userId);
    public CommentResponseDTO getAllComments();
    public int getTotalComments();
}
