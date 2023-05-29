package com.yondu.knowledgebase.controllers;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.Utils.MultipleSort;
import com.yondu.knowledgebase.services.PageService;

@RestController
@RequestMapping(path = "pages")
public class PageController {

    private final PageService pageService;

    /**
     * @param pageService
     */
    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping(path = "{id}")
    public PageDTO getPage(@PathVariable Long id) {
        return pageService.findById(id);
    }

    // @GetMapping(path = "directories/{directory_id}/pages/{page_id}")
    // public PageDTO getPage(@PathVariable(name = "directory_id") Long directoryId,
    // @PathVariable(name = "page_id") Long pageId) {
    // return pageService.findByDirectoryIdAndId(directoryId, pageId);
    // }

    @GetMapping
    public PaginatedResponse<PageDTO> getAllPages(
            @RequestParam(defaultValue = "", name = "search") String searchKey,
            @RequestParam(defaultValue = "1", name = "page") int pageNumber,
            @RequestParam(defaultValue = "50", name = "size") int pageSize,
            @RequestParam(defaultValue = "", name = "sort_by") String[] sortBy) {

        return pageService.findAll(searchKey, pageNumber, pageSize, sortBy);
    }

    @PostMapping
    public PageDTO savePageVersion() {
        return PageDTO.builder().build();
    }

    // public CustomPage<UserDto> getUsers(
    // @CurrentSecurityContext(expression = "authentication.getName()") String
    // currentUser,
    // @RequestParam(required = false) String role,
    // @RequestParam(defaultValue = "1") int page,
    // @RequestParam(defaultValue = "10") int size,
    // @RequestParam(defaultValue = "email,asc") String[] sortBy) {
    // int retrievedPage = Math.max(1, page);
    // Pageable paging = PageRequest.of(retrievedPage - 1, size,
    // Sort.by(MultipleSort.sortWithOrders(sortBy)));

    // Page<User> results = this.userService.findAllExceptCurrentUser(currentUser,
    // role, paging);
    // List<UserDto> userRequestList = results.getContent().stream()
    // .map(user -> new UserDto(user)).collect(Collectors.toList());
    // return new CustomPage<UserDto>(userRequestList, retrievedPage, size,
    // results.getTotalElements());
    // }
}
