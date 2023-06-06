package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.comment.CommentCountResponseDTO;
import com.yondu.knowledgebase.DTO.comment.CommentRequestDTO;
import com.yondu.knowledgebase.DTO.comment.CommentResponseDTO;
import com.yondu.knowledgebase.entities.Comment;

import java.util.List;


public interface CommentService {
    public CommentResponseDTO createComment(CommentRequestDTO commentRequestDTO, Long commentParentId);
    public List<CommentResponseDTO> getAllComments(String entity, Long id);

    CommentCountResponseDTO getTotalComments(String entity, Long id);

    CommentResponseDTO getComment(Long commentId);
}
