package com.yondu.knowledgebase.DTO.rights.page;

import com.yondu.knowledgebase.DTO.rights.RightsDTO;
import com.yondu.knowledgebase.entities.Page;

public class PageRightsDTO extends RightsDTO {
    public record BaseResponse(Long id, Page page){}
}
