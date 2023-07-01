package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PageVersionDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.entities.Page;
import com.yondu.knowledgebase.enums.PageType;

public interface PageService {

    public PageDTO findById(Long id);

    public PageDTO findById(PageType pageType, Long id);

    public PageDTO createNewPage(PageType pageType, Long directoryId, PageVersionDTO page);

    public PageDTO updatePageDraft(PageType pageType, Long pageId, Long versionId, PageVersionDTO page);

    public PageDTO deletePage(PageType pageType, Long pageId);

    public PageDTO updateActiveStatus(PageType pageType, Long pageId, Boolean isActive);

    public PageDTO updateCommenting(PageType pageType, Long pageId, Boolean allowCommenting);

    public Page getPage(PageType pageType, Long pageId);

    public PageDTO findByIdWithVersions(PageType pageType, Long pageId);

    public PaginatedResponse<PageDTO> findAllByFullTextSearch(PageType pageType, String searchKey,
            Long[] primaryKeys, String[] categories, String[] tags,
            Boolean isArchive, Boolean isPublished, Boolean exactSearch, Integer pageNumber,
            Integer pageSize, String[] sortBy);

    public Page getPage(Long pageId);

    public PaginatedResponse<PageDTO> findAllByDirectoryIdAndFullTextSearch(PageType pageType, Long directoryId,
            String searchKey,
            String[] categories, String[] tags,
            Boolean isArchive, Boolean isPublished, Boolean exactSearch, Integer pageNumber,
            Integer pageSize, String[] sortBy);

    public PaginatedResponse<PageDTO> findPagesByUser(int page, int size, String type, String[] sortBy);

    public PaginatedResponse<PageDTO> findAllPendingVersions(PageType pageType, String searchKey, Boolean isArchive,
            Boolean approverOnly, Integer pageNumber, Integer pageSize, String[] sortBy);

    public PaginatedResponse<PageDTO> findAllDraftVersions(PageType pageType, String searchKey, Boolean isArchive,
            Integer pageNumber, Integer pageSize, String[] sortBy);

    public Boolean getLockStatus(Long pageId, Boolean lockAfter);
}
