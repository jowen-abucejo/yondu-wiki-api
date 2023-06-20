package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.role.RoleDTO;
import com.yondu.knowledgebase.exceptions.RequestValidationException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.services.RoleService;
import org.springframework.data.domain.Page;
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

    @GetMapping("/roles/paginated")
    public ResponseEntity<ApiResponse<PaginatedResponse<RoleDTO>>> getAllRolesPaginated(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(roleService.getAllRolesPaginated(page, size), "Success retrieving list of roles"));
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<RoleDTO>>> getAllRoles(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(roleService.getAllRoles(), "Success retrieving list of roles"));
    }

    @PostMapping("/roles")
    public ResponseEntity<ApiResponse<RoleDTO>> addRole(@RequestBody RoleDTO roleDTO) {
        // Perform validation on the roleDTO object
        if (roleDTO.getRoleName() == null || roleDTO.getRoleName().isEmpty()) {
            throw new RequestValidationException("Role name is required");
        }

        RoleDTO addedRole = roleService.addRole(roleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(addedRole, "Role created successfully"));
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<ApiResponse<RoleDTO>> getRoleByID(@PathVariable Long id) {
        RoleDTO role = roleService.getRole(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(role, "Role with id: " + id + " found"));
    }

    @PostMapping("/roles/{id}")
    public ResponseEntity<ApiResponse<RoleDTO>> editRoleById(@RequestBody RoleDTO roleDTO, @PathVariable Long id) {
        RoleDTO updatedRole = roleService.editRoleById(id, roleDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(updatedRole, "Edit Successfully"));
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<ApiResponse<Long>> deleteRoleById(@PathVariable Long id) {
        roleService.deleteRoleById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(id, "Role with " +id+ " has been delete successfully"));
    }
}