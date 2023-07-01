package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.enums.PageType;

import org.springframework.http.ResponseEntity;
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
import com.yondu.knowledgebase.services.PageService;

import jakarta.validation.Valid;

@RestController
public class PageController {

    private final PageService pageService;
    private final PageType pageType = PageType.WIKI;

    /**
     * @param pageService
     */
    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping(path = "pages/{id}")
    public ApiResponse<PageDTO> getPage(@PathVariable Long id) {
        var page = pageService.findById(pageType, id);
        return new ApiResponse<PageDTO>("success", page, "Wiki retrieved");
    }

    @GetMapping(path = "pages/search")
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

    @GetMapping(path = "pages")
    public PaginatedResponse<PageDTO> getAllApprovedPages(
            @RequestParam(defaultValue = "", name = "categories") String[] categories,
            @RequestParam(defaultValue = "", name = "tags") String[] tags,
            @RequestParam(defaultValue = "1", name = "page") int pageNumber,
            @RequestParam(defaultValue = "50", name = "size") int pageSize,
            @RequestParam(defaultValue = "", name = "sortBy") String[] sortBy) {

        return pageService.findAllByFullTextSearch(pageType, "", new Long[] {}, categories, tags, false, true, false,
                pageNumber, pageSize, sortBy);
    }

    @GetMapping(path = "pages/{id}/versions")
    public ApiResponse<PageDTO> getPageWithVersions(@PathVariable(name = "id") Long pageId) {
        var page = pageService.findByIdWithVersions(pageType, pageId);
        return new ApiResponse<PageDTO>("success", page, "Wiki retrieved");
    }

    @PostMapping(path = "directories/{id}/pages")
    public ApiResponse<PageDTO> saveNewPage(@PathVariable(name = "id") Long directoryId,
            @RequestBody @Valid PageVersionDTO pageVersionDTO) {
        var page = pageService.createNewPage(pageType, directoryId, pageVersionDTO);
        return new ApiResponse<PageDTO>("success", page, "New wiki created");
    }

    @PutMapping(path = "pages/{pageId}/versions/{versionId}")
    public ApiResponse<PageDTO> updatePageVersion(@PathVariable Long pageId, @PathVariable Long versionId,
            @RequestBody @Valid PageVersionDTO pageVersionDTO) {
        var page = pageService.updatePageDraft(pageType, pageId, versionId, pageVersionDTO);
        return new ApiResponse<PageDTO>("success", page, "Wiki version updated");
    }

    @DeleteMapping(path = "pages/{pageId}/delete")
    public ApiResponse<PageDTO> deletePage(@PathVariable Long pageId) {
        var page = pageService.deletePage(pageType, pageId);
        return new ApiResponse<PageDTO>("success", page, "Wiki Deleted");
    }

    @PatchMapping(path = "pages/{pageId}/archive")
    public ApiResponse<PageDTO> archivePage(@PathVariable Long pageId) {
        var page = pageService.updateActiveStatus(pageType, pageId, false);
        return new ApiResponse<PageDTO>("success", page, "Wiki set as archive");

    }

    @PatchMapping(path = "pages/{pageId}/unarchive")
    public ApiResponse<PageDTO> unArchivePage(@PathVariable Long pageId) {
        var page = pageService.updateActiveStatus(pageType, pageId, true);
        return new ApiResponse<PageDTO>("success", page, "Wiki set as not archive");

    }

    @PatchMapping(path = "pages/{pageId}/settings/comments-off")
    public ApiResponse<PageDTO> turnOffCommenting(@PathVariable Long pageId) {
        var page = pageService.updateCommenting(pageType, pageId, false);
        return new ApiResponse<PageDTO>("success", page, "Wiki commenting off");

    }

    @PatchMapping(path = "pages/{pageId}/settings/comments-on")
    public ApiResponse<PageDTO> turnOnCommenting(@PathVariable Long pageId) {
        var page = pageService.updateCommenting(pageType, pageId, true);
        return new ApiResponse<PageDTO>("success", page, "Wiki commenting on");
    }

    @GetMapping(path = "directories/{id}/pages/search")
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

    @GetMapping(path = "pages/pending")
    /**
     * Retrieves a paginated list of wikis that are submitted for approval
     * and are accessible to the current user.
     * 
     * @param searchKey    The search key to filter the wikis.
     * 
     * @param archived     Flag indicating whether to retrieve only archived wikis
     *                     if true or active wikis only if false. Default is false.
     * 
     * @param approverOnly Flag indicating whether to retrieve only wikis that the
     *                     user can approve. Defaults is true.
     * 
     * @param pageNumber   The page number for pagination. Default is 1,
     * 
     * @param pageSize     The number of wikis to include per page. Default is 20.
     * 
     * @param sortBy       An array of fields to sort the wikis by. Default is an
     *                     empty array.
     * 
     * @return A paginated response containing a list of pending wiki versions
     *         accessible to the current user.
     */
    public PaginatedResponse<PageDTO> getAllPendingVersions(
            @RequestParam(defaultValue = "", name = "key") String searchKey,
            @RequestParam(defaultValue = "0", name = "archived") Boolean archived,
            @RequestParam(defaultValue = "1", name = "approverOnly") Boolean approverOnly,
            @RequestParam(defaultValue = "1", name = "page") int pageNumber,
            @RequestParam(defaultValue = "20", name = "size") int pageSize,
            @RequestParam(defaultValue = "", name = "sortBy") String[] sortBy) {

        return pageService.findAllPendingVersions(pageType, searchKey, archived, approverOnly, pageNumber, pageSize,
                sortBy);
    }

    @GetMapping(path = "pages/drafts")
    /**
     * Retrieves a paginated list of drafts of wikis that are accessible to the
     * current user.
     * 
     * @param searchKey  The search key to filter the wikis.
     * 
     * @param archived   Flag indicating whether to retrieve only archived wikis
     *                   if true or active wikis only if false. Default is false.
     * 
     * @param pageNumber The page number for pagination. Default is 1,
     * 
     * @param pageSize   The number of wikis to include per page. Default is 20.
     * 
     * @param sortBy     An array of fields to sort the wikis by. Default is an
     *                   empty array.
     * 
     * @return A paginated list of drafts of wikis that are accessible to the
     *         current user.
     */
    public PaginatedResponse<PageDTO> getAllDraftVersions(
            @RequestParam(defaultValue = "", name = "key") String searchKey,
            @RequestParam(defaultValue = "0", name = "archived") Boolean archived,
            @RequestParam(defaultValue = "1", name = "page") int pageNumber,
            @RequestParam(defaultValue = "20", name = "size") int pageSize,
            @RequestParam(defaultValue = "", name = "sortBy") String[] sortBy) {

        return pageService.findAllDraftVersions(pageType, searchKey, archived, pageNumber, pageSize, sortBy);
    }

    @GetMapping(path="pages/{id}/lock")
    /**
     * Retrieves a page lock status.
     * Gives the user choice to lock after
     * checking or not
     *
     * @param id         ID of the page you want to
     *                   check
     *
     * @param lockAfter  Locks the page after checking.
     *                   Default false.
     */
    public ResponseEntity getLockStatus(@PathVariable Long id, @RequestParam(defaultValue = "0", name = "lockAfter") Boolean lockAfter) {
        pageService.getLockStatus(id, lockAfter);
        return ResponseEntity.ok().build();
    }

}
