package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.post.PostSearchResult;
import com.yondu.knowledgebase.enums.PageType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostPageService {
    private final PostService postService;
    private final PageService pageService;

    public PostPageService(PostService postService, PageService pageService) {
        this.postService = postService;
        this.pageService = pageService;
    }

    public PaginatedResponse<Object> getAllPostsAndPageSortedByDateCreated(String searchKey, String[] categories, String[] tags, Boolean isArchive, Boolean exactSearch, int page, int size, String[] sortBy, Boolean published){
        PaginatedResponse<PostSearchResult> postPage = postService.findAllByFullTextSearch(searchKey, categories, tags, isArchive, exactSearch, page, size, sortBy);
        PaginatedResponse<PageDTO> pagePage = pageService.findAllByFullTextSearch(PageType.WIKI, searchKey, new Long[]{}, categories, tags, isArchive, published, exactSearch, page, size,  sortBy);
        PaginatedResponse<PageDTO> announcementPage = pageService.findAllByFullTextSearch(PageType.ANNOUNCEMENT, searchKey, new Long[]{}, categories, tags, isArchive, published, exactSearch, page, size,  sortBy);
        List<Object> contentList = new ArrayList<>();
        contentList.addAll(postPage.getData());
        contentList.addAll(pagePage.getData());
        contentList.addAll(announcementPage.getData());

        long total = postPage.getTotal() + pagePage.getTotal() + announcementPage.getTotal();

        PaginatedResponse<Object> result = new PaginatedResponse<>(contentList, page, size, total);
        System.out.println(result);
        return result;
    }


}
