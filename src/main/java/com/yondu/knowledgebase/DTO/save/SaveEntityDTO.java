package com.yondu.knowledgebase.DTO.save;

public class SaveEntityDTO {

    private Long entityId;

    public SaveEntityDTO(Long entityId) {
        this.entityId = entityId;
    }
    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }


}
