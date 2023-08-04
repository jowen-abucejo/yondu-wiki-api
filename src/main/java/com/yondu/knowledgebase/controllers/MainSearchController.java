package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.services.PageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainSearchController {
    private final PageService pageService;

    /**
     * @param pageService
     */
    public MainSearchController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping(path = "search-all")
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
            @RequestParam(defaultValue = "0", name = "days") Long days,
            @RequestParam(defaultValue = "0", name = "userIsAuthor") Boolean userIsAuthor,
            @RequestParam(defaultValue = "0", name = "saved") Boolean savedOnly,
            @RequestParam(defaultValue = "0", name = "upVoted") Boolean upVotedOnly,
            @RequestParam(defaultValue = "announcement,wiki,discussion", name = "contentTypes") String[] pageTypes) {

        return pageService.searchAll(pageTypes, searchKey, primaryKeys, categories, tags, archived,
                published, exactSearch, pageNumber, pageSize, days, userIsAuthor, savedOnly, upVotedOnly, sortBy);
    }
}
