package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.directory.permission.DirectoryPermissionRequest;
import com.yondu.knowledgebase.DTO.directory.permission.DirectoryPermissionResponse;
import com.yondu.knowledgebase.exceptions.BadRequestException;
import com.yondu.knowledgebase.exceptions.NotFoundException;
import com.yondu.knowledgebase.services.DirectoryPermissionService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/directory-permissions")
public class DirectoryPermissionController {
    private final DirectoryPermissionService directoryPermissionService;

    public DirectoryPermissionController(DirectoryPermissionService directoryPermissionService) {
        this.directoryPermissionService = directoryPermissionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DirectoryPermissionResponse>> createDirectoryPermission(@RequestBody DirectoryPermissionRequest request) {
        try {
            if (request.getName().isEmpty() || request.getDescription().isEmpty() || request.getName() == null || request.getDescription() == null) {
                throw new BadRequestException("Name and description are required.");
            }

            DirectoryPermissionResponse createdPermission = directoryPermissionService.createDirectoryPermission(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createdPermission, "Directory Permission created successfully"));

        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error("Directory Permission name already exists"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An error occurred: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DirectoryPermissionResponse>>> getAllDirectoryPermissions(){
        try {
            List<DirectoryPermissionResponse> permissions = directoryPermissionService.getAllDirectoryPermissions();
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(permissions, "Data retrieved successfully"));

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DirectoryPermissionResponse>> getDirectoryPermissionById(@PathVariable Long id) {
        try {
            DirectoryPermissionResponse permission = directoryPermissionService.getDirectoryPermissionByID(id);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(permission, "Data retrieved successfully"));

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An error occurred: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DirectoryPermissionResponse>> updateDirectoryPermission(@PathVariable Long id, @RequestBody DirectoryPermissionRequest request){
        try {
            if (request.getName().isEmpty() || request.getDescription().isEmpty() || request.getName() == null || request.getDescription() == null) {
                throw new BadRequestException("Name and description are required.");
            }

            DirectoryPermissionResponse updatedPermission = directoryPermissionService.updateDirectoryPermission(id, request);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(updatedPermission, "Directory Permission updated successfully"));

        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));

        } catch (DataIntegrityViolationException e ){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error("Directory Permission name already exists"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An error occurred: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DirectoryPermissionResponse>> deleteDirectoryPermission(@PathVariable Long id){
        try {
            DirectoryPermissionResponse deletedPermission = directoryPermissionService.deleteDirectoryPermission(id);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(deletedPermission, "Directory Permission deleted successfully"));

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An error occurred: " + e.getMessage()));
        }
    }
}
