package com.yondu.knowledgebase.DTO.rights;

import com.yondu.knowledgebase.entities.Rights;

public class RightsDTOMapper {
    public static RightsDTO.BaseResponse mapToBaseResponse(Rights rights) {
        return new RightsDTO.BaseResponse(
                rights.getId());
    }
}
