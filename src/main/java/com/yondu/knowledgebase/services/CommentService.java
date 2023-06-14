package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.comment.CommentDTO;

import java.util.List;


public interface CommentService {

    CommentDTO.BaseResponse createComment(CommentDTO.BaseRequest request, Long parentCommentId);
    public List<CommentDTO.BaseResponse> getAllComments(String entity, Long id);
    public List<CommentDTO.BaseComment> getAllParentComments(String entity, Long id);
    CommentDTO.CountResponse getTotalComments(String entity, Long id);
    CommentDTO.BaseResponse getComment (Long id);
    CommentDTO.BaseComment allowReply (Long id);

    CommentDTO.BaseComment deleteComment(Long id);

    List <CommentDTO.BaseComment> getReplies(Long commentId);
}
