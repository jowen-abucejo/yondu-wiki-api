package com.yondu.knowledgebase.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.role.RoleDTO;
import com.yondu.knowledgebase.exceptions.RequestValidationException;
import com.yondu.knowledgebase.services.RoleService;

@RestController
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/roles/paginated")
    public ResponseEntity<ApiResponse<PaginatedResponse<RoleDTO.PaginatedResponse>>> getAllRolesPaginated(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String searchKey
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(roleService.getAllRolesPaginated(page, size, searchKey), "Success retrieving list of roles")
        );
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
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(role, "Role found"));
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
                .body(ApiResponse.success(id, "Role has been delete successfully"));
    }
}