package com.yondu.knowledgebase.DTO.comment;

import com.yondu.knowledgebase.DTO.comment.CommentDTO.ShortRatedResponse;
import com.yondu.knowledgebase.DTO.user.UserDTO;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.Comment;
import com.yondu.knowledgebase.entities.User;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommentDTOMapper {

    public static Comment mapToComment(CommentDTO.BaseRequest request, User user, String entityType, Long entityId) {
        return new Comment(
                LocalDateTime.now(),
                request.comment(),
                entityId,
                entityType,
                user
        );
    }

    public static CommentDTO.ShortResponse mapToShortResponse(Comment comment) {
        Set<User> users = comment.getCommentMentions();
        Set<UserDTO.GeneralResponse> commentMentions = new HashSet<>();
        for (User user : users){
            commentMentions.add(UserDTOMapper.mapToGeneralResponse(user));
        }
        return new CommentDTO.ShortResponse(
                comment.getId(),
                comment.getDateCreated(),
                comment.getComment(),
                UserDTOMapper.mapToGeneralResponse(comment.getUser()),
                comment.getParentCommentId(),
                comment.getEntityId(),
                comment.getEntityType(),
                comment.isAllowReply(),
                commentMentions,
                (long) comment.getCommentReplies().size(),
                comment.isDeleted()
        );
    }

    public static ShortRatedResponse mapToShortRatedResponse(Comment comment, String voteType, Integer voteCount, Integer totalVoteCount) {
        Set<User> users = comment.getCommentMentions();
        Set<UserDTO.GeneralResponse> commentMentions = new HashSet<>();
        for (User user : users){
            commentMentions.add(UserDTOMapper.mapToGeneralResponse(user));
        }
        return new CommentDTO.ShortRatedResponse(
                comment.getId(),
                comment.getDateCreated(),
                comment.getComment(),
                UserDTOMapper.mapToGeneralResponse(comment.getUser()),
                comment.getParentCommentId(),
                comment.getEntityId(),
                comment.getEntityType(),
                comment.isAllowReply(),
                commentMentions,
                (long) comment.getCommentReplies().size(),
                comment.isDeleted(),
                voteType,
                voteCount,
                totalVoteCount
        );
    }

    public static CommentDTO.BaseRatedResponse mapToBaseRatedResponse(Comment comment, String voteType, Integer voteCount, Integer totalVoteCount) {
        CommentDTO.ShortRatedResponse shortRatedResponse = mapToShortRatedResponse(comment, voteType, voteCount, totalVoteCount);
        Set<Comment> replies = comment.getCommentReplies();
        List<CommentDTO.ShortResponse> commentReplies = new ArrayList<>();
        for (Comment reply: replies){
            commentReplies.add(CommentDTOMapper.mapToShortResponse(reply));
        }
        return new CommentDTO.BaseRatedResponse(
                shortRatedResponse,
                commentReplies
        );
    }

    public static CommentDTO.BaseResponse mapToBaseResponse(Comment comment) {
        CommentDTO.ShortResponse shortResponse = mapToShortResponse(comment);
        Set<Comment> replies = comment.getCommentReplies();
        List<CommentDTO.ShortResponse> commentReplies = new ArrayList<>();
        for (Comment reply: replies){
            commentReplies.add(CommentDTOMapper.mapToShortResponse(reply));
        }
        return new CommentDTO.BaseResponse(
                shortResponse,
                commentReplies
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
