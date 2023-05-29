package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.entities.Role;
import com.yondu.knowledgebase.exceptions.AccessDeniedException;
import com.yondu.knowledgebase.exceptions.BadRequestException;
import com.yondu.knowledgebase.exceptions.NotFoundException;
import com.yondu.knowledgebase.services.RoleService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RoleController {

    private RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/role")
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {

        ApiResponse<List<Role>> response = new ApiResponse<>();

        try {
            response.setStatus("Success");
            response.setData(roleService.getAllRoles());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Handle the exception, log the error, and return an appropriate response
            response.setStatus("error");
            response.setErrorMessage("Failed to retrieve list of roles");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/role")
    public ResponseEntity<ApiResponse<Role>> addRole(@RequestBody Role role) {

        try {
            // Perform validation on the role object
            if (role.getRoleName() == null || role.getRoleName().isEmpty()) {
                throw new BadRequestException("Role name is required");
            }

            Role addedRole = roleService.addRole(role);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("success", addedRole, "Role created successfully"));

        } catch (BadRequestException e) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }
    @GetMapping("/role/{id}")
    public ResponseEntity<ApiResponse<Role>> getRoleByID(@PathVariable Long id) {

        ApiResponse<Role> response = new ApiResponse<>();

        try {
            Role role = roleService.getRole(id);
            if (role != null) {
                response.setStatus("Success");
                response.setData(role);
                return ResponseEntity.ok(response);
            } else {
                response.setStatus("error");
                response.setErrorMessage("Role not found!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            // Handle the exception, log the error, and return an appropriate response
            e.printStackTrace();
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }

    private ResponseEntity<ApiResponse<Role>> createErrorResponse(HttpStatus status, String errorMessage) {
        return ResponseEntity.status(status).body(new ApiResponse<>("error", null, errorMessage));
    }


}