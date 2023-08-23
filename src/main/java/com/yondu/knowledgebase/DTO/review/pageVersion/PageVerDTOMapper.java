package com.yondu.knowledgebase.DTO.review.pageVersion;

import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.PageVersion;

public class PageVerDTOMapper {


    public static PageVerDTO.BaseResponse mapToBaseResponse(PageVersion version) {
        return new PageVerDTO.BaseResponse(version.getId(), version.getTitle(),version.getContent(), PageDTOrMapper.mapToBaseResponse(version.getPage()),
                version.getDateModified(), UserDTOMapper.mapToBaseResponse(version.getModifiedBy()));
    }
}


