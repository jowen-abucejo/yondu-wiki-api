package com.yondu.knowledgebase.DTO.page_rights;

import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.Page;

public class PageDTOMapper {

    public static PageDTO.BaseResponse mapToBaseResponse(Page page) {
        return new PageDTO.BaseResponse(page.getId(), page.getDateCreated(),
                UserDTOMapper.mapToShortResponse(page.getAuthor()),page.getActive(),
                page.getDeleted());
    }

}
