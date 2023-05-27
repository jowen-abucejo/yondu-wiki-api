package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.directory.permission.DirectoryPermissionRequest;
import com.yondu.knowledgebase.DTO.directory.permission.DirectoryPermissionResponse;
import com.yondu.knowledgebase.services.DirectoryPermissionService;
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
    public ResponseEntity<ApiResponse<DirectoryPermissionResponse>> createDirectoryPermission(@RequestBody DirectoryPermissionRequest directoryPermissionRequest){
        //api response
        ApiResponse<DirectoryPermissionResponse> response = new ApiResponse<>();
        try {
            if (directoryPermissionRequest.getName().isEmpty() || directoryPermissionRequest.getDescription().isEmpty()){
                throw new NullPointerException("One or more required parameters are missing or null.");
            }
            DirectoryPermissionResponse createdPermission = directoryPermissionService.createDirectoryPermission(directoryPermissionRequest);
            response.setStatus("success");
            response.setData(createdPermission);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus("error");
            response.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DirectoryPermissionResponse>>> getAllDirectoryPermissions(){
        //api response
        ApiResponse<List<DirectoryPermissionResponse>> response = new ApiResponse<>();
        try {
            List<DirectoryPermissionResponse> permissions = directoryPermissionService.getAllDirectoryPermissions();
            response.setStatus("success");
            response.setData(permissions);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            response.setStatus("error");
            response.setErrorMessage("Failed to retrieve list of directory permissions");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DirectoryPermissionResponse>> getDirectoryPermissionById(@PathVariable Long id){
        //api response
        ApiResponse<DirectoryPermissionResponse> response = new ApiResponse<>();
        try {
            DirectoryPermissionResponse permission = directoryPermissionService.getDirectoryPermissionByID(id);
            response.setStatus("success");
            response.setData(permission);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus("error");
            response.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse<DirectoryPermissionResponse>> updateDirectoryPermission(@PathVariable Long id, @RequestBody DirectoryPermissionRequest directoryPermissionRequest){
        //api response
        ApiResponse<DirectoryPermissionResponse> response = new ApiResponse<>();
        try {
            if (directoryPermissionRequest.getName().isEmpty() || directoryPermissionRequest.getDescription().isEmpty()){
                throw new NullPointerException("One or more required parameters are missing or null.");
            }
            DirectoryPermissionResponse updatedPermission = directoryPermissionService.updateDirectoryPermission(id, directoryPermissionRequest);
            response.setStatus("success");
            response.setData(updatedPermission);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus("error");
            response.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/{id}/delete")
    public ResponseEntity<ApiResponse<DirectoryPermissionResponse>> deleteDirectoryPermission(@PathVariable Long id){
        //api response
        ApiResponse<DirectoryPermissionResponse> response = new ApiResponse<>();
        try {
            DirectoryPermissionResponse deletedPermission = directoryPermissionService.deleteDirectoryPermission(id);
            response.setStatus("success");
            response.setData(deletedPermission);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            response.setStatus("error");
            response.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
