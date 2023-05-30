package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.role.RoleDTO;
import com.yondu.knowledgebase.exceptions.RequestValidationException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.services.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/role")
    public ResponseEntity<ApiResponse<List<RoleDTO>>> getAllRoles() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(roleService.getAllRoles(), "Success retrieving list of roles"));
        } catch (Exception e) {
            // Handle the exception, log the error, and return an appropriate response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Failed to retrieve list of roles!"));

        }
    }

    @PostMapping("/role")
    public ResponseEntity<ApiResponse<RoleDTO>> addRole(@RequestBody RoleDTO roleDTO) {
        try {
            // Perform validation on the roleDTO object
            if (roleDTO.getRoleName() == null || roleDTO.getRoleName().isEmpty()) {
                throw new RequestValidationException("Role name is required");
            }

            RoleDTO addedRole = roleService.addRole(roleDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(addedRole, "Role created successfully"));

        } catch (RequestValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An error occurred!"));

        }
    }

    @GetMapping("/role/{id}")
    public ResponseEntity<ApiResponse<RoleDTO>> getRoleByID(@PathVariable Long id) {
        try {
            RoleDTO role = roleService.getRole(id);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(role, "Role with id: " + id + " found"));

        } catch (ResourceNotFoundException e) {
            // Handle the exception, log the error, and return an appropriate response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Role with id: " + id + "not found"));
        } catch (Exception e) {
            // Handle the exception, log the error, and return an appropriate response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An error occurred!"));
        }
    }

    @PostMapping("/role/{id}")
    public ResponseEntity<ApiResponse<RoleDTO>> editRoleById(@RequestBody RoleDTO roleDTO, @PathVariable Long id) {
        try {
            RoleDTO updatedRole = roleService.editRoleById(id, roleDTO);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(updatedRole, "Edit Successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An error occurred!"));
        }
    }

    @DeleteMapping("/role/{id}")
    public ResponseEntity<ApiResponse<Long>> deleteRoleById(@PathVariable Long id) {
        try {
            roleService.deleteRoleById(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(id, "Role with " +id+ " has been delete successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (RequestValidationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An error occurred!"));
        }
    }
}