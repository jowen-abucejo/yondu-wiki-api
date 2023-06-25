package com.yondu.knowledgebase.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PageVersionDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.enums.PageType;
import com.yondu.knowledgebase.services.PageService;

import jakarta.validation.Valid;

@RestController
public class AnnouncementController {

    private final PageService pageService;
    private final PageType pageType = PageType.ANNOUNCEMENT;

    /**
     * @param pageService
     */
    public AnnouncementController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping(path = "announcements/{id}")
    public ApiResponse<PageDTO> getPage(@PathVariable Long id) {
        var page = pageService.findById(pageType, id);
        return new ApiResponse<PageDTO>("success", page, "Announcement retrieved");
    }

    @GetMapping(path = "announcements/search")
    public PaginatedResponse<PageDTO> searchPages(
            @RequestParam(defaultValue = "", name = "key") String searchKey,
            @RequestParam(defaultValue = "", name = "categories") String[] categories,
            @RequestParam(defaultValue = "", name = "tags") String[] tags,
            @RequestParam(defaultValue = "0", name = "archived") Boolean archived,
            @RequestParam(defaultValue = "1", name = "published") Boolean published,
            @RequestParam(defaultValue = "0", name = "exactSearch") Boolean exactSearch,
            @RequestParam(defaultValue = "1", name = "page") int pageNumber,
            @RequestParam(defaultValue = "20", name = "size") int pageSize,
            @RequestParam(defaultValue = "", name = "sortBy") String[] sortBy,
            @RequestParam(defaultValue = "", name = "ids") Long[] primaryKeys) {

        return pageService.findAllByFullTextSearch(pageType, searchKey, primaryKeys, categories, tags, archived,
                published, exactSearch, pageNumber, pageSize, sortBy);
    }

    @GetMapping(path = "announcements")
    public PaginatedResponse<PageDTO> getAllApprovedPages(
            @RequestParam(defaultValue = "", name = "categories") String[] categories,
            @RequestParam(defaultValue = "", name = "tags") String[] tags,
            @RequestParam(defaultValue = "1", name = "page") int pageNumber,
            @RequestParam(defaultValue = "50", name = "size") int pageSize,
            @RequestParam(defaultValue = "", name = "sortBy") String[] sortBy) {

        return pageService.findAllByFullTextSearch(pageType, "", new Long[] {}, categories, tags, false, true, false,
                pageNumber, pageSize, sortBy);
    }

    @GetMapping(path = "announcements/{id}/versions")
    public ApiResponse<PageDTO> getPageWithVersions(@PathVariable(name = "id") Long pageId) {
        var page = pageService.findByIdWithVersions(pageType, pageId);
        return new ApiResponse<PageDTO>("success", page, "Announcement retrieved");
    }

    @PostMapping(path = "directories/{id}/announcements")
    public ApiResponse<PageDTO> saveNewPage(@PathVariable(name = "id") Long directoryId,
            @RequestBody @Valid PageVersionDTO pageVersionDTO) {
        var page = pageService.createNewPage(pageType, directoryId, pageVersionDTO);
        return new ApiResponse<PageDTO>("success", page, "New announcement created");
    }

    @PutMapping(path = "announcements/{pageId}/versions/{versionId}")
    public ApiResponse<PageDTO> updatePageVersion(@PathVariable Long pageId, @PathVariable Long versionId,
            @RequestBody @Valid PageVersionDTO pageVersionDTO) {
        var page = pageService.updatePageDraft(pageType, pageId, versionId, pageVersionDTO);
        return new ApiResponse<PageDTO>("success", page, "Announcement version updated");
    }

    @DeleteMapping(path = "announcements/{pageId}/delete")
    public ApiResponse<PageDTO> deletePage(@PathVariable Long pageId) {
        var page = pageService.deletePage(pageType, pageId);
        return new ApiResponse<PageDTO>("success", page, "Announcement deleted");
    }

    @PatchMapping(path = "announcements/{pageId}/archive")
    public ApiResponse<PageDTO> archivePage(@PathVariable Long pageId) {
        var page = pageService.updateActiveStatus(pageType, pageId, false);
        return new ApiResponse<PageDTO>("success", page, "Announcement set as archive");

    }

    @PatchMapping(path = "announcements/{pageId}/unarchive")
    public ApiResponse<PageDTO> unArchivePage(@PathVariable Long pageId) {
        var page = pageService.updateActiveStatus(pageType, pageId, true);
        return new ApiResponse<PageDTO>("success", page, "Announcement set as not archive");

    }

    @PatchMapping(path = "announcements/{pageId}/settings/comments-off")
    public ApiResponse<PageDTO> turnOffCommenting(@PathVariable Long pageId) {
        var page = pageService.updateCommenting(pageType, pageId, false);
        return new ApiResponse<PageDTO>("success", page, "Announcement commenting off");

    }

    @PatchMapping(path = "announcements/{pageId}/settings/comments-on")
    public ApiResponse<PageDTO> turnOnCommenting(@PathVariable Long pageId) {
        var page = pageService.updateCommenting(pageType, pageId, true);
        return new ApiResponse<PageDTO>("success", page, "Announcement commenting on");
    }

    @GetMapping(path = "directories/{id}/announcements/search")
    public PaginatedResponse<PageDTO> searchPagesInDirectory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "", name = "key") String searchKey,
            @RequestParam(defaultValue = "", name = "categories") String[] categories,
            @RequestParam(defaultValue = "", name = "tags") String[] tags,
            @RequestParam(defaultValue = "0", name = "archived") Boolean archived,
            @RequestParam(defaultValue = "1", name = "published") Boolean published,
            @RequestParam(defaultValue = "0", name = "exactSearch") Boolean exactSearch,
            @RequestParam(defaultValue = "1", name = "page") int pageNumber,
            @RequestParam(defaultValue = "20", name = "size") int pageSize,
            @RequestParam(defaultValue = "", name = "sortBy") String[] sortBy) {

        return pageService.findAllByDirectoryIdAndFullTextSearch(pageType, id, searchKey, categories, tags, archived,
                published, exactSearch, pageNumber, pageSize, sortBy);
    }
}
