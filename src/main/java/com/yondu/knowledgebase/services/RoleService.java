package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.entities.Role;
import com.yondu.knowledgebase.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles(){
        return roleRepository.findAll();
    }

    public Role getRole(Long id) {
        return roleRepository.findById(id).orElseThrow();
    }

    public Role addRole(Role role) {
        roleRepository.save(role);

        return role;
    }
}
