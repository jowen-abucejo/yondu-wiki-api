package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.entities.Role;
import com.yondu.knowledgebase.exceptions.BadRequestException;
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

        List<Role> roles = roleRepository.findAll();

        for(Role existingRole : roles){
            if(existingRole.getRoleName().equals(role.getRoleName())){
                throw new BadRequestException("Role already exists");
            }
        }

        roleRepository.save(role);
        return role;
    }
}
