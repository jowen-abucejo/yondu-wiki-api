package com.yondu.knowledgebase.DTO.comment;

import com.yondu.knowledgebase.DTO.user.UserDTO;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.Comment;
import com.yondu.knowledgebase.entities.User;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommentDTOMapper {

    public static Comment mapToComment(CommentDTO.BaseRequest request, User user) {
        return new Comment(
                LocalDateTime.now(),
                request.comment(),
                request.entityId(),
                request.entityType(),
                user
        );
    }

    public static CommentDTO.BaseComment mapToBaseComment(Comment comment) {
        Set<User> users = comment.getCommentMentions();
        Set<UserDTO.GeneralResponse> commentMentions = new HashSet<>();
        for (User user : users){
            commentMentions.add(UserDTOMapper.mapToGeneralResponse(user));
        }
        return new CommentDTO.BaseComment(
                comment.getId(),
                comment.getDateCreated(),
                comment.getComment(),
                UserDTOMapper.mapToGeneralResponse(comment.getUser()),
                comment.getParentCommentId(),
                comment.getEntityId(),
                comment.getEntityType(),
                comment.isAllowReply(),
                commentMentions
        );
    }

    public static CommentDTO.BaseResponse mapToBaseResponse(Comment comment, Long totalReplies, List<CommentDTO.BaseComment> replies) {
        CommentDTO.BaseComment baseComment = mapToBaseComment(comment);
        return new CommentDTO.BaseResponse(
                baseComment,
                totalReplies,
                replies
        );
    }

    public static CommentDTO.CountResponse mapToCountResponse(String entityType, Long entityId, Long totalCommentCount) {
        return new CommentDTO.CountResponse(
                entityId,
                entityType,
                totalCommentCount
        );
    }


}
