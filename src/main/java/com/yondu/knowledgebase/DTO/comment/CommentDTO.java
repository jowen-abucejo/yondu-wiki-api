package com.yondu.knowledgebase.DTO.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class CommentDTO {
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public record BaseRequest(String comment, @JsonProperty("entity_id") Long entityId, @JsonProperty("entity_type") String entityType, @JsonProperty("comment_mentions") Long[] commentMentions) {}
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public record BaseComment (Long id, LocalDateTime dataCreated, String comment, UserDTO.GeneralResponse user, Long parentCommentId, Long entityId, String entityType, boolean allowReply, Set<UserDTO.GeneralResponse> commentMentions){}
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public record BaseResponse (CommentDTO.BaseComment comment, Long totalReplies, List<CommentDTO.BaseComment> replies){}
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public record CountResponse (Long entityId, String entityType, Long totalComment){}
}
