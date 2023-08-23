package com.yondu.knowledgebase.DTO.save;

import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.Save;

public class SaveDTOMapper {

    public static SaveDTO.BaseResponse mapToBaseResponse(Save save) {
        return new SaveDTO.BaseResponse(
                save.getId(),
                UserDTOMapper.mapToBaseResponse(save.getAuthor()),
                save.getDateCreated(),
                save.getEntityType(),
                save.getEntityId()
        );
    }

}
