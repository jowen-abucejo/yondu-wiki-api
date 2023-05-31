package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.permission.PermissionDTO;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.services.PermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
    @GetMapping("/permission")
    public ResponseEntity<ApiResponse<List<PermissionDTO.BaseResponse>>> getAllPermission(){
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(permissionService.getAllPermission(), "Success retrieving list of permissions"));
    }

    @GetMapping("/permission/{id}")
    public ResponseEntity<ApiResponse<PermissionDTO.BaseResponse>> getPermissionById(@PathVariable Long id){
        PermissionDTO.BaseResponse permission = permissionService.getPermission(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(permission, "Permission with id: " + id + " found"));
    }

}
