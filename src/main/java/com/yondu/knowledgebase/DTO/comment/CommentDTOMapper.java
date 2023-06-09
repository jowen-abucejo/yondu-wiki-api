package com.yondu.knowledgebase.DTO.comment;

import com.yondu.knowledgebase.DTO.user.UserDTO;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.Comment;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.CommentRepository;
import com.yondu.knowledgebase.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CommentDTOMapper {

    private static UserRepository userRepository;
    private static CommentRepository commentRepository;

    public CommentDTOMapper(UserRepository userRepository, CommentRepository commentRepository) {
        CommentDTOMapper.userRepository = userRepository;
        CommentDTOMapper.commentRepository = commentRepository;
    }

    public static Comment mapToComment(CommentDTO.BaseRequest request, User user) {
        return new Comment(
                LocalDateTime.now(),
                request.comment(),
                request.entityId(),
                request.entityType(),
                user,
                getMentionedUsers(request.mentionedUsersId())
        );
    }
    public static CommentDTO.BaseComment mapToBaseComment(Comment comment) {
        return new CommentDTO.BaseComment(
                comment.getId(),
                comment.getDateCreated(),
                comment.getComment(),
                comment.getParentCommentId(),
                comment.getEntityId(),
                comment.getEntityType(),
                mapUsersToShortResponse(comment.getCommentMentions())
        );
    }

    public static CommentDTO.ShortResponse mapToShortResponse(Comment comment) {
        return new CommentDTO.ShortResponse(
                comment.getId(),
                comment.getDateCreated(),
                comment.getComment(),
                comment.getUser().getId(),
                comment.getCommentMentions()
        );
    }

    public static CommentDTO.BaseResponse mapToBaseResponse(Comment comment) {
        CommentDTO.BaseComment baseComment = mapToBaseComment(comment);
        List<CommentDTO.ShortResponse> replies = getAllReplies(comment);
        return new CommentDTO.BaseResponse(
                baseComment,
                commentRepository.countAllReplies(comment.getId()),
                replies
        );
    }

    public static CommentDTO.CountResponse mapToCountResponse(String entityType,Long entityId) {
        Long totalCommentCount = commentRepository.countByEntityTypeAndEntityId(entityType,entityId);
        return new CommentDTO.CountResponse(
                entityId,
                entityType,
                totalCommentCount
        );
    }

    private static Set<User> getMentionedUsers(Long [] userIds){
        Set<User> mentionedUsers = new HashSet<>();
        for (Long userId : userIds) {
            User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException(String.format("User ID not found : %d",userId)));
            if (user != null) {
                mentionedUsers.add(user);
            }
        }
        return mentionedUsers;
    }

    private static Set<UserDTO.ShortResponse> mapUsersToShortResponse(Set<User> users){
        Set<UserDTO.ShortResponse> mentionedUsers = new HashSet<>();
        for (User user : users) {
            if (user != null) {
                mentionedUsers.add(UserDTOMapper.mapToShortResponse(user));
            }
        }
        return mentionedUsers;
    }


    private static List<CommentDTO.ShortResponse> getAllReplies (Comment comment){
        List<Comment> comments = commentRepository.findAllCommentReplies(comment.getEntityType(),comment.getEntityId(),comment.getId());
        return comments.stream().map(CommentDTOMapper::mapToShortResponse).collect(Collectors.toList());
    }
}

