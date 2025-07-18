package com.yondu.knowledgebase.services;

import java.util.List;

import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PageVersionDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.enums.PageType;

public interface PageService {

    public PageDTO findById(Long id);

    public PageDTO findById(PageType pageType, Long id);

    public PageDTO createNewPage(PageType pageType, Long directoryId, PageVersionDTO page);

    public PageDTO updatePageDraft(PageType pageType, Long pageId, Long versionId, PageVersionDTO page);

    public PageDTO deletePage(PageType pageType, Long pageId);

    public PageDTO updateActiveStatus(PageType pageType, Long pageId, Boolean isActive);

    public PageDTO updateCommenting(PageType pageType, Long pageId, Boolean allowCommenting);

    public PageDTO findByIdWithVersions(PageType pageType, Long pageId);

    public PaginatedResponse<PageDTO> findAllByDirectoryIdAndFullTextSearch(PageType pageType, Long directoryId,
            String searchKey, String[] categories, String[] tags,
            Boolean isArchived, Boolean isPublished, Boolean exactSearch, Integer pageNumber,
            Integer pageSize, String[] sortBy);

    public PaginatedResponse<PageDTO> findAllPendingVersions(PageType pageType, String searchKey, Integer pageNumber,
            Integer pageSize, String[] sortBy);

    public PaginatedResponse<PageDTO> findAllDraftVersions(PageType pageType, String searchKey, Integer pageNumber,
            Integer pageSize, String[] sortBy);

    public PageDTO movePageToDirectory(PageType pageType, Long directoryId, Long pageId);

    public PageDTO findVersion(PageType pageType, Long pageId, Long versionId);

    public PageDTO markAsRead(PageType pageType, Long pageId);

    public List<PageDTO> getUnreadPages(PageType pageType);

    public PaginatedResponse<PageDTO> searchAll(String[] pageTypeFilter, String searchKey,
            Long[] primaryKeys, String[] categories, String[] tags,
            Boolean isArchived, Boolean isPublished, Boolean exactSearch, Integer pageNumber,
            Integer pageSize, Long days, String author, Boolean savedOnly,
            Boolean upVotedOnly, String[] sortBy);
}
