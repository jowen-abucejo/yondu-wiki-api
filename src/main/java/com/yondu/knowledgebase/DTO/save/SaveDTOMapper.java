package com.yondu.knowledgebase.DTO.save;

import com.yondu.knowledgebase.entities.Save;
import com.yondu.knowledgebase.entities.User;

public class SaveDTOMapper {

    public static SaveDTO.Base mapEntityToBase(Save save) {
        return new SaveDTO.Base(save.getId(),
                save.getAuthor().getId(),
                save.getDateCreated());
    }

    public static Save mapBaseToEntity(SaveDTO.BaseRequest base) {
        return new Save(
                new User(base.authorId()),
                base.entityType(),
                base.entityId()
        );
    }

    public static SaveDTO.BaseResponse mapEntityToBaseResponse(Save save) {
        return new SaveDTO.BaseResponse(
                save.getId(),
                save.getAuthor().getId(),
                save.getDateCreated(),
                save.getEntityType(),
                save.getEntityId()
        );
    }
}
