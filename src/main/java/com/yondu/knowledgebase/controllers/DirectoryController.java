package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.directory.DirectoryRequest;
import com.yondu.knowledgebase.DTO.directory.DirectoryResponse;
import com.yondu.knowledgebase.DTO.directory.role_access.DirectoryRoleAccessRequest;
import com.yondu.knowledgebase.DTO.directory.role_access.DirectoryRoleAccessResponse;
import com.yondu.knowledgebase.entities.DirectoryRoleAccess;
import com.yondu.knowledgebase.services.DirectoryService;
import com.yondu.knowledgebase.services.DirectoryRoleAccessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/directories")
public class DirectoryController {
    private final DirectoryService directoryService;
    private final DirectoryRoleAccessService directoryRoleAccessService;
    public DirectoryController(DirectoryService directoryService, DirectoryRoleAccessService directoryRoleAccessService) {
        this.directoryService = directoryService;
        this.directoryRoleAccessService = directoryRoleAccessService;
    }

    @PostMapping("/{parentId}/create")
    public ResponseEntity<Object> createDirectory(@PathVariable("parentId") Long parentId, @RequestBody DirectoryRequest directoryRequest) {
        try {
            if(directoryRequest.getName() == null || directoryRequest.getName().isEmpty() || directoryRequest.getDescription() == null || directoryRequest.getDescription().isEmpty()) {
                throw new NullPointerException();
            }
            DirectoryResponse response = directoryService.createDirectory(parentId, directoryRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/rename")
    public ResponseEntity<Object> renameDirectory(@PathVariable("id") Long id, @RequestBody DirectoryRequest directoryRequest) {
        try {
            if(directoryRequest.getName() == null || directoryRequest.getName().isEmpty()) {
                throw new NullPointerException();
            }
            DirectoryResponse response = directoryService.renameDirectory(id, directoryRequest.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/remove")
    public ResponseEntity<Object> deleteDirectory(@PathVariable("id") Long id) {
        try {
            String response = directoryService.removeDirectory(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
}
