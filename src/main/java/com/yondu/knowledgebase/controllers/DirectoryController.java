package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.directory.DirectoryRequest;
import com.yondu.knowledgebase.DTO.directory.DirectoryResponse;
import com.yondu.knowledgebase.DTO.directory.request.CreateDirectoryRequest;
import com.yondu.knowledgebase.DTO.directory.request.RenameDirectoryRequest;
import com.yondu.knowledgebase.DTO.directory.role_access.DirectoryRoleAccessRequest;
import com.yondu.knowledgebase.DTO.directory.role_access.DirectoryRoleAccessResponse;
import com.yondu.knowledgebase.exceptions.BadRequestException;
import com.yondu.knowledgebase.exceptions.NotFoundException;
import com.yondu.knowledgebase.services.DirectoryService;
import com.yondu.knowledgebase.services.DirectoryRoleAccessService;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@RestController
@RequestMapping("/directories")
public class DirectoryController {
    private final DirectoryService directoryService;
    private final DirectoryRoleAccessService directoryRoleAccessService;

    public DirectoryController(DirectoryService directoryService, DirectoryRoleAccessService directoryRoleAccessService) {
        this.directoryService = directoryService;
        this.directoryRoleAccessService = directoryRoleAccessService;
    }

    @PostMapping("/{parentId}")
    public ResponseEntity<ApiResponse<Object>> createDirectory(@PathVariable("parentId") Long parentId, @RequestBody CreateDirectoryRequest request) {
        try {
            if (request.getName().isEmpty() || request.getDescription().isEmpty()) {
                throw new BadRequestException("Invalid request body");
            }

            Object data = directoryService.createDirectory(parentId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("success", data, "Directory created successfully"));

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

    @PutMapping("/{id}/rename")
    public ResponseEntity<ApiResponse<Object>> renameDirectory(@PathVariable("id") Long id, @RequestBody RenameDirectoryRequest request) {
        try {
            if (request.getName().isEmpty()) {
                throw new BadRequestException("Invalid request body");
            }

            Object data = directoryService.renameDirectory(id, request.getName());
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", data, "Directory renamed successfully"));

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
    public ResponseEntity<ApiResponse<Object>> deleteDirectory(@PathVariable("id") Long id) {
        try {
            directoryService.removeDirectory(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", null, "Directory deleted successfully"));
        } catch (NotFoundException e) {
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }


//  DIRECTORY ROLE ACCESS
    @PostMapping("/{directoryId}/manage-permissions")
    public ResponseEntity<ApiResponse<DirectoryRoleAccessResponse>> addDirectoryRoleAccess(@PathVariable Long directoryId, @RequestBody DirectoryRoleAccessRequest request){
        //api response
        ApiResponse<DirectoryRoleAccessResponse> response = new ApiResponse<>();
        try {
            if (request.getRoleId() == null || request.getPermissionId() == null) {
                throw new NullPointerException("One or more required parameters are missing or null");
            }
            DirectoryRoleAccessResponse addDirectoryRoleAccess = directoryRoleAccessService.addDirectoryRoleAccess(directoryId, request.getRoleId(), request.getPermissionId());
            response.setStatus("success");
            response.setData(addDirectoryRoleAccess);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            response.setStatus("error");
            response.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{directoryId}/manage-permissions")
    public ResponseEntity<ApiResponse<List<DirectoryRoleAccessResponse>>> getAllDirectoryRoleAccess(@PathVariable Long directoryId){
        //api response
        ApiResponse<List<DirectoryRoleAccessResponse>> response = new ApiResponse<>();
        try {
            List<DirectoryRoleAccessResponse> roleAccesses = directoryRoleAccessService.getAllDirectoryRoleAccess(directoryId);
            response.setStatus("success");
            response.setData(roleAccesses);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus("error");
            response.setErrorMessage("Failed to retrieve list of directory role accesses");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    //removeRoledirectoryaccess


    private ResponseEntity<ApiResponse<Object>> createErrorResponse(HttpStatus status, String errorMessage) {
        return ResponseEntity.status(status).body(new ApiResponse<>("error", null, errorMessage));
    }
}
