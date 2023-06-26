package com.yondu.knowledgebase.DTO.email;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.time.LocalDateTime;

public class EmailDTO {
    public record GeneralRequest (String to, String from, @JsonProperty("from_profile") String fromProfile, @JsonProperty("notification_type") String notificationType, @JsonProperty("contentType") String contentType, @JsonProperty("content_link") String contentLink){}
    public record NewUserRequest (String to, String from, @JsonProperty("temporary_password") String temporaryPassword, @JsonProperty("notification_type") String notificationType){}
    public record BaseResponse (UserDTO.ShortResponse to, UserDTO.ShortResponse from, String notificationType, LocalDateTime timeStamp){}
}
