package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.entities.Role;
import com.yondu.knowledgebase.services.RoleService;
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

    @GetMapping("/role/{id}")
    public Role getRole(@PathVariable Long id){
        return roleService.getRole(id);
    }

    @PostMapping("/role")
    public Role addRole(@RequestBody Role role){
        return roleService.addRole(role);
    }
}
