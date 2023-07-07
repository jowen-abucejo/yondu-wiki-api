package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.role.RoleDTO;
import com.yondu.knowledgebase.services.PostPageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainSearchController {

    private final PostPageService postPageService;

    public MainSearchController(PostPageService postPageService) {
        this.postPageService = postPageService;
    }

    @GetMapping("/main-search")
    public ResponseEntity<ApiResponse<PaginatedResponse<Object>>> getAllMainSearch(
            @RequestParam(defaultValue = "", name = "key") String searchKey,
            @RequestParam(defaultValue = "", name = "categories") String[] categories,
            @RequestParam(defaultValue = "", name = "tags") String[] tags,
            @RequestParam(defaultValue = "false", name = "archived") Boolean archived,
            @RequestParam(defaultValue = "true", name = "exactSearch") Boolean exactSearch,
            @RequestParam(defaultValue = "1", name = "page") int pageNumber,
            @RequestParam(defaultValue = "15", name = "size") int pageSize,
            @RequestParam(defaultValue = "", name = "sortBy") String[] sortBy,
            @RequestParam(defaultValue = "true", name = "published") Boolean published
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(postPageService.getAllPostsAndPageSortedByDateCreated(searchKey, categories, tags, archived, exactSearch, pageNumber, pageSize, sortBy, published), "Success retrieving")
        );
    }
}
