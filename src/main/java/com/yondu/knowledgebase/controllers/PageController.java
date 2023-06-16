package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.entities.Page;
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

    /**
     * @param pageService
     */
    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping(path = "pages/{id}")
    public ApiResponse<PageDTO> getPage(@PathVariable Long id) {
        var page = pageService.findById(id);
        return new ApiResponse<PageDTO>("success", page, "Page retrieved");
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
            @RequestParam(defaultValue = "", name = "sortBy") String[] sortBy) {

        return pageService.findAllByFullTextSearch(searchKey, categories, tags, archived, published, exactSearch,
                pageNumber, pageSize, sortBy);
    }

    @GetMapping(path = "pages")
    public PaginatedResponse<PageDTO> getAllPagesWithVersions(
            @RequestParam(defaultValue = "", name = "categories") String[] categories,
            @RequestParam(defaultValue = "", name = "tags") String[] tags,
            @RequestParam(defaultValue = "1", name = "page") int pageNumber,
            @RequestParam(defaultValue = "50", name = "size") int pageSize,
            @RequestParam(defaultValue = "", name = "sortBy") String[] sortBy) {

        return pageService.findAllByFullTextSearch("", categories, tags, false, true, false,
                pageNumber, pageSize, sortBy);
    }

    @GetMapping(path = "pages/{id}/versions")
    public ApiResponse<PageDTO> getPageWithVersions(@PathVariable(name = "id") Long pageId) {
        var page = pageService.findByIdWithVersions(pageId);
        return new ApiResponse<PageDTO>("success", page, "Page retrieved");
    }

    @PostMapping(path = "directories/{id}/pages")
    public ApiResponse<PageDTO> saveNewPage(@PathVariable(name = "id") Long directoryId,
            @RequestBody @Valid PageVersionDTO pageVersionDTO) {
        var page = pageService.createNewPage(directoryId, pageVersionDTO);
        return new ApiResponse<PageDTO>("success", page, "New page created");
    }

    @PutMapping(path = "pages/{pageId}/versions/{versionId}")
    public ApiResponse<PageDTO> updatePageVersion(@PathVariable Long pageId, @PathVariable Long versionId,
            @RequestBody @Valid PageVersionDTO pageVersionDTO) {
        var page = pageService.updatePageDraft(pageId, versionId, pageVersionDTO);
        return new ApiResponse<PageDTO>("success", page, "Page version updated");
    }

    @DeleteMapping(path = "pages/{pageId}/delete")
    public ApiResponse<PageDTO> deletePage(@PathVariable Long pageId) {
        var page = pageService.deletePage(pageId);
        return new ApiResponse<PageDTO>("success", page, "Page Deleted");
    }

    @PatchMapping(path = "pages/{pageId}/archive")
    public ApiResponse<PageDTO> archivePage(@PathVariable Long pageId) {
        var page = pageService.updateActiveStatus(pageId, false);
        return new ApiResponse<PageDTO>("success", page, "Page set as archive");

    }

    @PatchMapping(path = "pages/{pageId}/unarchive")
    public ApiResponse<PageDTO> unArchivePage(@PathVariable Long pageId) {
        var page = pageService.updateActiveStatus(pageId, true);
        return new ApiResponse<PageDTO>("success", page, "Page set as not archive");

    }

    @PatchMapping(path = "pages/{pageId}/settings/comments-off")
    public ApiResponse<PageDTO> turnOffCommenting(@PathVariable Long pageId) {
        var page = pageService.updateCommenting(pageId, false);
        return new ApiResponse<PageDTO>("success", page, "Page commenting off");

    }

    @PatchMapping(path = "pages/{pageId}/settings/comments-on")
    public ApiResponse<PageDTO> turnOnCommenting(@PathVariable Long pageId) {
        var page = pageService.updateCommenting(pageId, true);
        return new ApiResponse<PageDTO>("success", page, "Page commenting on");
    }

    @PostMapping(path = "directories/{id}/announcements")
    public ApiResponse<PageDTO> saveNewAnnouncement(@PathVariable(name = "id") Long directoryId,
            @RequestBody @Valid PageVersionDTO pageVersionDTO) {
        var page = pageService.createNewAnnouncement(directoryId, pageVersionDTO);
        return new ApiResponse<PageDTO>("success", page, "New announcement created");
    }
}
