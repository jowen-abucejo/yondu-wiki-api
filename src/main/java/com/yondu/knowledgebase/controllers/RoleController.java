package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.entities.Role;
import com.yondu.knowledgebase.services.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RoleController {

    private RoleService roleService;

    public RoleController(RoleService roleService){
        this.roleService = roleService;
    }

    @GetMapping("/role")
    public List<Role> getAllRoles(){
        return roleService.getAllRoles();
    }


    @GetMapping("/role/{$id}")
    public ResponseEntity<Role> getRoleByID(@PathVariable Long id) {
        Role role = roleService.getRole(id);
        return (role != null) ? ResponseEntity.ok(role) : ResponseEntity.notFound().build();
    }

}
