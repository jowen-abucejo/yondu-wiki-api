package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.directory.DirectoryDTO;
import com.yondu.knowledgebase.DTO.directory.user_access.DirectoryUserAccessDTO;
import com.yondu.knowledgebase.exceptions.AccessDeniedException;
import com.yondu.knowledgebase.exceptions.BadRequestException;
import com.yondu.knowledgebase.exceptions.NotFoundException;
import com.yondu.knowledgebase.services.DirectoryService;
import com.yondu.knowledgebase.services.DirectoryUserAccessService;
import org.springframework.dao.DataIntegrityViolationException;
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

    @PostMapping("/{parentId}")
    public ResponseEntity<ApiResponse<?>> createDirectory(@PathVariable("parentId") Long parentId, @RequestBody DirectoryDTO.CreateRequest request) {
        try {
            if (request.name().isEmpty() || request.description().isEmpty()) {
                throw new BadRequestException("Invalid request body");
            }

            Object data = directoryService.createDirectory(parentId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Directory created successfully"));

        } catch (BadRequestException e) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());

        } catch (NotFoundException e) {
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());

        } catch (AccessDeniedException e) {
            return createErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());

        } catch (DataIntegrityViolationException e) {
            return createErrorResponse(HttpStatus.CONFLICT, "Directory already exists");

        } catch (Exception e) {
            e.printStackTrace();
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }

    @PutMapping("/{id}/rename")
    public ResponseEntity<ApiResponse<?>> renameDirectory(@PathVariable("id") Long id, @RequestBody DirectoryDTO.RenameRequest request) {
        try {
            if (request.name().isEmpty()) {
                throw new BadRequestException("Invalid request body");
            }

            Object data = directoryService.renameDirectory(id, request);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "Directory renamed successfully"));

        } catch (BadRequestException e) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());

        } catch (NotFoundException e) {
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());

        } catch (DataIntegrityViolationException e) {
            return createErrorResponse(HttpStatus.CONFLICT, "Directory already exists");

        } catch (Exception e) {
            e.printStackTrace();
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred");
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteDirectory(@PathVariable("id") Long id) {
        try {
            directoryService.removeDirectory(id);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null, "Directory deleted successfully"));
        } catch (NotFoundException e) {
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }


//  DIRECTORY USER ACCESS
    @PostMapping("/{directoryId}/permissions")
    public ResponseEntity<ApiResponse<?>> addDirectoryUserAccess(@PathVariable Long directoryId, @RequestBody DirectoryUserAccessDTO.AddRequest request){
        try {
            if (request.userId() == null || request.permissionId() == null) {
                throw new BadRequestException("User ID and Permission ID are required");
            }

            DirectoryUserAccessDTO.BaseResponse addDirectoryUserAccess = directoryUserAccessService.addDirectoryUserAccess(directoryId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(addDirectoryUserAccess, "Directory User Access added successfully"));

        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/{directoryId}/permissions")
    public ResponseEntity<ApiResponse<List<?>>> getAllDirectoryUserAccess(@PathVariable Long directoryId){
        try {
            List<DirectoryUserAccessDTO.BaseResponse> userAccesses = directoryUserAccessService.getAllDirectoryUserAccess(directoryId);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(userAccesses, "Data retrieved successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An error occurred: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{directoryId}/permissions/{id}")
    public ResponseEntity<ApiResponse<?>> removeDirectoryUserAccess (@PathVariable Long directoryId, Long id){
        try {
            directoryUserAccessService.removeDirectoryUserAccess(directoryId, id);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null, "Directory User Access removed successfully"));

        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An error occurred: " + e.getMessage()));
        }
    }

    private ResponseEntity<ApiResponse<?>> createErrorResponse(HttpStatus status, String errorMessage) {
        return ResponseEntity.status(status).body(ApiResponse.error(errorMessage));
    }
}
