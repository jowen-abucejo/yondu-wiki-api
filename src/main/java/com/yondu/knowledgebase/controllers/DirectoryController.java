package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.directory.DirectoryDTO;
import com.yondu.knowledgebase.DTO.directory.user_access.DirectoryUserAccessDTO;
import com.yondu.knowledgebase.services.DirectoryService;
import com.yondu.knowledgebase.services.DirectoryUserAccessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/directories")
public class DirectoryController {
    private final DirectoryService directoryService;
    private final DirectoryUserAccessService directoryUserAccessService;

    public DirectoryController(DirectoryService directoryService, DirectoryUserAccessService directoryUserAccessService) {
        this.directoryService = directoryService;
        this.directoryUserAccessService = directoryUserAccessService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getDirectory(@PathVariable("id") Long id) {
        DirectoryDTO.GetResponse data = directoryService.getDirectory(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Directory found"));
    }

    @PostMapping("/{parentId}")
    public ResponseEntity<ApiResponse<?>> createDirectory(@PathVariable("parentId") Long parentId, @RequestBody DirectoryDTO.CreateRequest request) {
        DirectoryDTO.BaseResponse data = directoryService.createDirectory(parentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Directory created successfully"));
    }

    @PutMapping("/{id}/rename")
    public ResponseEntity<ApiResponse<?>> renameDirectory(@PathVariable("id") Long id, @RequestBody DirectoryDTO.RenameRequest request) {
        DirectoryDTO.BaseResponse data = directoryService.renameDirectory(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "Directory renamed successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteDirectory(@PathVariable("id") Long id) {
        directoryService.removeDirectory(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null, "Directory deleted successfully"));
    }


    //  DIRECTORY USER ACCESS
    @PostMapping("/{directoryId}/permissions")
    public ResponseEntity<ApiResponse<?>> addDirectoryUserRights(@PathVariable Long directoryId, @RequestBody DirectoryUserAccessDTO.AddRequest request) {
        DirectoryUserAccessDTO.BaseResponse data = directoryUserAccessService.addDirectoryUserRights(directoryId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Directory User Rights added successfully"));
    }

    @GetMapping("/{directoryId}/permissions")
    public ResponseEntity<ApiResponse<List<?>>> getAllDirectoryUserRights(@PathVariable Long directoryId) {
        List<DirectoryUserAccessDTO.BaseResponse> data = directoryUserAccessService.getAllDirectoryUserRights(directoryId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "Data retrieved successfully"));
    }

    @DeleteMapping("/{directoryId}/permissions/{userAccessId}")
    public ResponseEntity<ApiResponse<?>> removeDirectoryUserAccess(@PathVariable Long directoryId, @PathVariable Long userAccessId) {
        directoryUserAccessService.removeDirectoryUserRights(directoryId, userAccessId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null, "Directory User Rights removed successfully"));
    }
}
