package com.yondu.knowledgebase.controllers;

import java.util.List;

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

    @GetMapping(path = "announcements")
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
            @RequestParam(defaultValue = "", name = "ids") Long[] primaryKeys,
            @RequestParam(defaultValue = "", name = "days") Long days,
            @RequestParam(defaultValue = "0", name = "isAuthor") Boolean isAuthor,
            @RequestParam(defaultValue = "0", name = "saved") Boolean savedOnly,
            @RequestParam(defaultValue = "0", name = "upVoted") Boolean upVotedOnly) {

        return pageService.searchAll(new String[] { pageType.getCode() }, searchKey, primaryKeys,
                categories, tags, archived, published, exactSearch,
                pageNumber, pageSize, days, isAuthor, savedOnly, upVotedOnly, sortBy);
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

    @GetMapping(path = "directories/{id}/announcements")
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

    @GetMapping(path = "announcements/pending")
    /**
     * Retrieves a paginated list of announcements that are submitted for
     * approval and are accessible to the current user.
     * 
     * @param searchKey  The search key to filter the announcements.
     * 
     * @param pageNumber The page number for pagination. Default is 1,
     * 
     * @param pageSize   The number of announcements to include per page. Default
     *                   is 20.
     * 
     * @param sortBy     An array of fields to sort the announcements by. Default
     *                   is an empty array.
     * 
     * @return A paginated response containing a list of pending announcement
     *         versions accessible to the current user.
     */
    public PaginatedResponse<PageDTO> getAllPendingVersions(
            @RequestParam(defaultValue = "", name = "key") String searchKey,
            @RequestParam(defaultValue = "0", name = "archived") Boolean archived,
            @RequestParam(defaultValue = "1", name = "page") int pageNumber,
            @RequestParam(defaultValue = "20", name = "size") int pageSize,
            @RequestParam(defaultValue = "", name = "sortBy") String[] sortBy) {

        return pageService.findAllPendingVersions(pageType, searchKey, pageNumber, pageSize,
                sortBy);
    }

    @GetMapping(path = "announcements/drafts")
    /**
     * Retrieves a paginated list of drafts of announcements that are accessible to
     * the current user.
     * 
     * @param searchKey  The search key to filter the announcements.
     * 
     * @param archived   Flag indicating whether to retrieve only archived
     *                   announcements if true or active announcements only if
     *                   false. Default is false.
     * 
     * @param pageNumber The page number for pagination. Default is 1,
     * 
     * @param pageSize   The number of announcements to include per page. Default is
     *                   20.
     * 
     * @param sortBy     An array of fields to sort the announcements by. Default is
     *                   an empty array.
     * 
     * @return A paginated list of drafts of announcements that are accessible to
     *         the current user.
     */
    public PaginatedResponse<PageDTO> getAllDraftVersions(
            @RequestParam(defaultValue = "", name = "key") String searchKey,
            @RequestParam(defaultValue = "0", name = "archived") Boolean archived,
            @RequestParam(defaultValue = "1", name = "page") int pageNumber,
            @RequestParam(defaultValue = "20", name = "size") int pageSize,
            @RequestParam(defaultValue = "", name = "sortBy") String[] sortBy) {

        return pageService.findAllDraftVersions(pageType, searchKey, archived, pageNumber, pageSize, sortBy);
    }

    @PatchMapping(path = "announcements/{id}/change-directory")
    public ApiResponse<PageDTO> movePageToDirectory(@PathVariable Long id,
            @RequestParam("directoryId") Long directoryId) {
        var page = pageService.movePageToDirectory(pageType, directoryId, id);
        return new ApiResponse<PageDTO>("success", page, "Page moved to the new directory");
    }

    @GetMapping(path = "announcements/{pageId}/versions/{versionId}")
    public ApiResponse<PageDTO> getPageVersion(@PathVariable Long pageId, @PathVariable Long versionId) {
        var page = pageService.findVersion(pageType, pageId, versionId);
        return new ApiResponse<PageDTO>("success", page, "Page version retrieved");
    }

    @PutMapping(path = "announcements/{pageId}/mark-as-read")
    public ApiResponse<PageDTO> markAsRead(@PathVariable Long pageId) {
        var page = pageService.markAsRead(pageType, pageId);
        return new ApiResponse<PageDTO>("success", page, "Page marked as read");
    }

    @GetMapping(path = "announcements/unread")
    public ApiResponse<List<PageDTO>> getUnreadPages() {
        var pages = pageService.getUnreadPages(pageType);
        return new ApiResponse<List<PageDTO>>("success", pages, "Unread pages retrieved");
    }
}
