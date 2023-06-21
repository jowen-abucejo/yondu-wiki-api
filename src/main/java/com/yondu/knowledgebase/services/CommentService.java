package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.comment.CommentDTO;

import java.util.List;


public interface CommentService {

    CommentDTO.BaseResponse createComment(CommentDTO.BaseRequest request, Long parentCommentId, String entityType, Long entityId);
    public List<CommentDTO.BaseResponse> getAllComments(String entity, Long id);
    public List<CommentDTO.ShortResponse> getAllParentComments(String entity, Long id);
    CommentDTO.CountResponse getTotalComments(String entity, Long id);
    CommentDTO.BaseResponse getComment (Long id);
    CommentDTO.ShortResponse allowReply (Long id, boolean allowReply);

    CommentDTO.ShortResponse deleteComment(Long id, boolean delete);

    List <CommentDTO.ShortResponse> getReplies(Long commentId);

    List <CommentDTO.ShortResponse>  searchComments (String key);
}
