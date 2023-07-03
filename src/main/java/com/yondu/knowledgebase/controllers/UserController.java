package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PageVersionDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.review.ReviewDTO;
import com.yondu.knowledgebase.DTO.user.UserDTO;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.*;
import com.yondu.knowledgebase.enums.PageType;
import com.yondu.knowledgebase.repositories.UserRepository;
import com.yondu.knowledgebase.services.PageService;
import com.yondu.knowledgebase.services.ReviewService;
import com.yondu.knowledgebase.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("users")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PageService pageService;
    @Autowired
    private ReviewService reviewService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<PaginatedResponse<UserDTO.WithRolesResponse>>> getAllUser(
            @RequestParam(defaultValue = "") String searchKey,
            @RequestParam(defaultValue = "") String statusFilter,
            @RequestParam(defaultValue = "") String roleFilter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        log.info("UserController.getAllUser()");
        log.info("searchKey: " + searchKey);
        log.info("statusFilter: " + statusFilter);
        log.info("roleFilter: " + roleFilter);
        log.info("page: " + page);
        log.info("size: " + size);

        ResponseEntity<ApiResponse<PaginatedResponse<UserDTO.WithRolesResponse>>> response;

        PaginatedResponse<UserDTO.WithRolesResponse> fetchedUsers = userService.getAllUser(searchKey, statusFilter, roleFilter, page, size);

        ApiResponse<PaginatedResponse<UserDTO.WithRolesResponse>> apiResponse = ApiResponse.success(fetchedUsers, "success");
        response = ResponseEntity.ok(apiResponse);

        return response;
    }

    @GetMapping("/search/permissions/{permissionId}")
    public ResponseEntity<ApiResponse<PaginatedResponse<UserDTO.ShortResponse>>> getUsersByPermission(
            @RequestParam(defaultValue = "") String searchKey,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "30") int size,
            @PathVariable Long permissionId
    ){
        log.info("getUsersByPermission()");
        log.info("searchKey : " + searchKey);
        log.info("page : " + page);
        log.info("size : " + size);
        log.info("permissionId : " + permissionId);

        searchKey = "%" + searchKey + "%";

        PaginatedResponse<UserDTO.ShortResponse> users = userService.getUsersByPermission(searchKey, page, size, permissionId);
        return ResponseEntity.ok(ApiResponse.success(users, "success"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO.WithRolesResponse>> getUserById(@PathVariable Long id){
        log.info("UserController.getUserById()");
        log.info("id : " + id);

        User user = userService.getUserById(id);

        UserDTO.WithRolesResponse userDTO = UserDTOMapper.mapToWithRolesResponse(user);

        ApiResponse apiResponse = ApiResponse.success(userDTO, "success");
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_USERS')")
    public ResponseEntity<ApiResponse<UserDTO.WithRolesResponse>> createNewUser(@RequestBody UserDTO.WithRolesRequest user) {
        log.info("UserController.createNewUser()");
        log.info("user : " + user.toString());

        User createdUser = userService.createNewUser(user);
        UserDTO.WithRolesResponse userResponse = UserDTOMapper.mapToWithRolesResponse(createdUser);
        ApiResponse apiResponse = ApiResponse.success(userResponse, "success");

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/deactivate")
    @PreAuthorize("hasAuthority('DEACTIVATE_USERS')")
    public ResponseEntity<ApiResponse<UserDTO.WithRolesResponse>> deactivateUser(@RequestBody UserDTO.ShortRequest user) {
        log.info("UserController.deactivateUser()");
        log.info("user : " + user.toString());

        User deactivatedUser = userService.deactivateUser(user);

        UserDTO.GeneralResponse userResponse = UserDTOMapper.mapToGeneralResponse(deactivatedUser);
        ApiResponse apiResponse = ApiResponse.success(userResponse, "success");

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/activate")
    @PreAuthorize("hasAuthority('DEACTIVATE_USERS')")
    public ResponseEntity<ApiResponse<UserDTO.WithRolesResponse>> activateUser(@RequestBody UserDTO.ShortRequest user) {
        log.info("UserController.activateUser()");
        log.info("user : " + user.toString());

        User activatedUser = userService.activateUser(user);

        UserDTO.GeneralResponse userResponse = UserDTOMapper.mapToGeneralResponse(activatedUser);
        ApiResponse apiResponse = ApiResponse.success(userResponse, "success");

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("{id}/update")
    @PreAuthorize("hasAuthority('UPDATE_USERS')")
    public ResponseEntity<ApiResponse<UserDTO.WithRolesResponse>> updateUser(@PathVariable long id, @RequestBody UserDTO.WithRolesRequest user) {
        log.info("UserController.updateUser()");
        log.info("id : " + id);
        log.info("user : " + user);

        User updatedUser = userService.updateUser(id, user);
        UserDTO.WithRolesResponse response = UserDTOMapper.mapToWithRolesResponse(updatedUser);

        return ResponseEntity.ok(ApiResponse.success(response, "success"));
    }

    /*
     *  TODO
     *   Create endpoint uploading new profile picture
     *   Wait for S3 Bucket, Secret, and Region
     */
    @PostMapping("/update/photo")
    public ResponseEntity<ApiResponse<UserDTO.GeneralResponse>> changeProfilePhoto(@RequestBody UserDTO.ChangePhotoRequest path) {
        log.info("UserController.changeProfilePhoto()");
        log.info("path : " + path);

        User user = userService.changeProfilePhoto(path);
        UserDTO.GeneralResponse userResponse = UserDTOMapper.mapToGeneralResponse(user);

        return ResponseEntity.ok(ApiResponse.success(userResponse, "Successfully changed your photo."));
    }

    @PostMapping("/update/password")
    public ResponseEntity<ApiResponse<UserDTO.BaseResponse>> updatePassword(@RequestBody UserDTO.ChangePassRequest request) {
        log.info("UserController.updatePassword");
        log.info("request : " + request);

        User user = userService.updatePassword(request);
        UserDTO.BaseResponse userResponse = UserDTOMapper.mapToBaseResponse(user);

        ApiResponse apiResponse = ApiResponse.success(userResponse, "Successfully changed password");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO.GeneralResponse>> viewMyProfile() {
        log.info("UserController.viewMyProfile()");

        User user = userService.viewMyProfile();
        UserDTO.GeneralResponse userDTO = UserDTOMapper.mapToGeneralResponse(user);
        ApiResponse response = ApiResponse.success(userDTO, "success");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/profile")
    public ResponseEntity<ApiResponse<UserDTO.WithRolesResponse>> updateProfile(@RequestBody UserDTO.WithRolesRequest user) {
        log.info("UserController.updateProfile()");
        log.info("user : " + user);

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User updatedUser = userService.updateUser(currentUser.getId(), user);
        UserDTO.WithRolesResponse updatedUserDTO = UserDTOMapper.mapToWithRolesResponse(updatedUser);

        return ResponseEntity.ok(ApiResponse.success(updatedUserDTO, "Successfully updated your profile."));
    }

    @GetMapping("/pages")
    public ResponseEntity<ApiResponse<PaginatedResponse<PageDTO>>> findPagesByUser(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "WIKI") String type,
            @RequestParam(defaultValue = "", name = "sortBy") String[] sortBy) {
        log.info("UserController.findPagesByUser()");
        log.info("page : " + page);
        log.info("size : " + size);
        log.info("type : " + type);

        PaginatedResponse<PageDTO> pages = pageService.findPagesByUser(page, size, type, sortBy);

        return ResponseEntity.ok(ApiResponse.success(pages, "Successfully retrieved pages."));
    }

    @GetMapping("/pages/{id}")
    public ResponseEntity<ApiResponse<PageDTO>> findPage(@PathVariable Long id){
        log.info("UserController.findPage()");
        log.info("id : " + id);

        Page p = pageService.getPage(PageType.WIKI, id);

        PageVersion pv = p.getPageVersions()
                .stream()
                .sorted(Comparator.comparing(PageVersion::getDateModified))
                .findFirst().get();
        PageDTO pageDTO = new PageDTO.PageDTOBuilder()
                .id(p.getId())
                .dateCreated(p.getDateCreated())
                .lockedBy(new com.yondu.knowledgebase.DTO.page.UserDTO.UserDTOBuilder()
                        .id(p.getLockedBy().getId())
                        .email(p.getLockedBy().getEmail())
                        .firstName(p.getLockedBy().getFirstName())
                        .lastName(p.getLockedBy().getLastName())
                        .position(p.getLockedBy().getPosition())
                        .build())
                .lockStart(p.getLockStart())
                .lockEnd(p.getLockEnd())
                .allowComment(p.getAllowComment())
                .author(new com.yondu.knowledgebase.DTO.page.UserDTO.UserDTOBuilder()
                        .id(p.getAuthor().getId())
                        .email(p.getAuthor().getEmail())
                        .firstName(p.getAuthor().getFirstName())
                        .lastName(p.getAuthor().getLastName())
                        .position(p.getAuthor().getPosition())
                        .build()
                )
                .active(p.getActive())
                .pageType(p.getType())
                .tags(p.getTags().stream().map(Tag::getName).collect(Collectors.toList()).toArray(new String[0]))
                .categories(p.getCategories().stream().map(Category::getName).collect(Collectors.toList()).toArray(new String[0]))
                .body(new PageVersionDTO.PageVersionDTOBuilder()
                        .id(pv.getId())
                        .content(pv.getOriginalContent())
                        .title(pv.getTitle())
                        .build()
                )
                .build();

        return ResponseEntity.ok(ApiResponse.success(pageDTO, "success"));
    }

    /**
     * Fetch pending(default) review requests
     * based on the authenticated user.
     * The status can be changed.
     *
     * @param page   page number
     * @param size   size per page
     * @param status (default = PENDING)
     *
     * @return PaginatedResponse<ReviewDTO.BaseResponse>
     */
    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<PaginatedResponse<ReviewDTO.BaseResponse>>> getReviewRequestsForUser(@RequestParam(defaultValue = "1") int page,
                                                                                                           @RequestParam(defaultValue = "10") int size,
                                                                                                           @RequestParam(defaultValue = "PENDING") String status) {
        log.info("UserController.getReviewsByUser()");
        log.info("page : " + page);
        log.info("size : " + size);
        log.info("status : " + status);

        PaginatedResponse<ReviewDTO.BaseResponse> reviews = reviewService.getReviewRequestForUser(page, size, status);
        return ResponseEntity.ok(ApiResponse.success(reviews, "Successfully retrieved reviews"));
    }
}
