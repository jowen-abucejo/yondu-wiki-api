package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PageVersionDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;

public interface PageService {

    public PageDTO findById(Long id);

    public PaginatedResponse<PageDTO> findAll(String searchKey, Integer pageNumber, Integer pageSize, String[] sortBy);

    public PaginatedResponse<PageDTO> findAllVersionsByTagsAndCategories(String[] categories, String[] tags,
            Integer pageNumber, Integer pageSize,
            String[] sortBy);

    public PageDTO createNewPage(Long directoryId, PageVersionDTO page);

    public PageDTO updatePageDraft(Long pageId, Long versionId, PageVersionDTO page);

    public PageDTO deletePage(Long pageId);

    public PageDTO updateActiveStatus(Long pageId, Boolean isActive);

    public PageDTO updateCommenting(Long pageId, Boolean allowCommenting);
}
