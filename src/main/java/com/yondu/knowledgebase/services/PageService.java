package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PageVersionDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.entities.Page;

public interface PageService {

        public PageDTO findById(Long id);

        public PageDTO createNewPage(Long directoryId, PageVersionDTO page);

        public PageDTO updatePageDraft(Long pageId, Long versionId, PageVersionDTO page);

        public PageDTO deletePage(Long pageId);

        public PageDTO updateActiveStatus(Long pageId, Boolean isActive);

        public PageDTO updateCommenting(Long pageId, Boolean allowCommenting);

        public Page getPage(Long pageId);

        public PaginatedResponse<PageDTO> findAllByFullTextSearch(String searchKey, String[] categories, String[] tags,
                        Boolean isArchive, Boolean isPublished, Boolean exactSearch, Integer pageNumber,
                        Integer pageSize, String[] sortBy);

}
