package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.post.PostDTO;
import com.yondu.knowledgebase.enums.PageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class PostPageService {
    private final PostService postService;
    private final PageService pageService;

    public PostPageService(PostService postService, PageService pageService) {
        this.postService = postService;
        this.pageService = pageService;
    }

    public PaginatedResponse<Object> getAllPostsAndPageSortedByDateCreated(int page, int size, String searchKey){
        PaginatedResponse<PostDTO> postPage = postService.getAllPost(page, size, searchKey);
        PaginatedResponse<PageDTO> pagePage = pageService.findAllByFullTextSearch(PageType.WIKI, searchKey, new Long[]{}, new String[]{}, new String[]{}, false, true, true, page, size,  new String[]{});
        PaginatedResponse<PageDTO> announcementPage = pageService.findAllByFullTextSearch(PageType.ANNOUNCEMENT, searchKey, new Long[]{}, new String[]{}, new String[]{}, false, true, true, page, size,  new String[]{});
        List<Object> contentList = new ArrayList<>();
        contentList.addAll(postPage.getData());
        contentList.addAll(pagePage.getData());
        contentList.addAll(announcementPage.getData());

        long total = postPage.getSize() + pagePage.getSize() + announcementPage.getSize();

        PaginatedResponse<Object> result = new PaginatedResponse<>(contentList, page, size, total);
        System.out.println(result);
        return result;
    }


}
