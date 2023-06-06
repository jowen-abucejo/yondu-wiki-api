package com.yondu.knowledgebase.DTO.save;


import java.time.LocalDateTime;

public class SaveDTO {


    public record Base(long id, long authorId, LocalDateTime dateCreated) {}

    public record BaseRequest(long authorId, String entityType, Long entityId) {}

    public record BaseResponse(long id, long authorId, LocalDateTime dateCreated , String entityType, Long entityId) {}
}
