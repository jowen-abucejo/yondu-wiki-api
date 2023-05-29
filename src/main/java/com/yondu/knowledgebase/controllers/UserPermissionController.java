package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.entities.Permission;
import com.yondu.knowledgebase.services.UserPermissionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserPermissionController {

    private final UserPermissionService userPermissionService;

    public UserPermissionController(UserPermissionService userPermissionService) {
        this.userPermissionService = userPermissionService;
    }
    @GetMapping("/user-permission")
    public List<Permission> getAllPermission(){
        return userPermissionService.getAllPermission();
    }

    @PostMapping("/user-permission")
    public Permission addPermission(@RequestBody Permission permission){
        return userPermissionService.addPermission(permission);
    }
}
