package com.yondu.knowledgebase.DTO.review.pageVersion;

import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.Page;

public class PageDTOrMapper {

    public static PageDTOr.BaseResponse mapToBaseResponse(Page page) {
        return new PageDTOr.BaseResponse(page.getId(), page.getDateCreated(),
                UserDTOMapper.mapToBaseResponse(page.getAuthor()),page.getActive(), page.getType());
    }
}
