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
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "") String searchKey
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(postPageService.getAllPostsAndPageSortedByDateCreated(page, size, searchKey), "Success retrieving")
        );
    }
}
