package com.yondu.knowledgebase.DTO.comment;

import com.yondu.knowledgebase.DTO.user.UserDTO;
import com.yondu.knowledgebase.entities.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class CommentDTO {
    public record BaseComment (Long id, LocalDateTime dataCreated, String comment, Long parentCommentId, Long entityId, String entityType, Set<UserDTO.ShortResponse> mentionedUsers){}
    public record ShortResponse (Long id, LocalDateTime dateCreated, String comment, Long userId, Set<User> mentionedUsers){}
    public record BaseRequest(String comment, Long userId, Long entityId, String entityType, Long[] mentionedUsersId) {}
    public record BaseResponse (CommentDTO.BaseComment comment, Long totalReplies, List<CommentDTO.ShortResponse> replies){}
    public record CountResponse (Long entityId, String entityType, Long totalComment){}
}
