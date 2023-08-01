package com.yondu.knowledgebase.DTO.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class CommentDTO {
    public record BaseRequest (String comment, @JsonProperty("comment_mentions") Long[] commentMentions) {}
    public record ShortResponse (Long id, LocalDateTime dataCreated, String comment, UserDTO.GeneralResponse user, Long parentCommentId, Long entityId, String entityType, boolean allowReply, Set<UserDTO.GeneralResponse> commentMentions, Long totalReplies, boolean deleted){}
    public record ShortRatedResponse (Long id, LocalDateTime dataCreated, String comment, UserDTO.GeneralResponse user, Long parentCommentId, Long entityId, String entityType, Boolean allowReply, Set<UserDTO.GeneralResponse> commentMentions, Long totalReplies, Boolean deleted, String voteType, Integer voteCount, Integer totalVoteCount){}
    public record BaseResponse (ShortResponse comment, List<ShortResponse> replies){}
    public record BaseRatedResponse (ShortRatedResponse comment, List<ShortResponse> replies){}
    public record CountResponse (Long entityId, String entityType, Long totalComment){}
}
