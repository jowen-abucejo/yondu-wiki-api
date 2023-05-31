package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.Comment.CommentDTO;
import com.yondu.knowledgebase.DTO.Comment.CommentResponseDTO;
import com.yondu.knowledgebase.entities.Comment;


public interface CommentService {
    public CommentDTO createComment(CommentDTO commentRequestDTO, Long commentParentId);
    public CommentResponseDTO getAllComments(Long pageId);
    public int getTotalComments();
    Comment getComment(Long commentId);
}
