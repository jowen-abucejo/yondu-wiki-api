package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.role.RoleDTO;
import com.yondu.knowledgebase.entities.Role;
import com.yondu.knowledgebase.exceptions.BadRequestException;
import com.yondu.knowledgebase.exceptions.NotFoundException;
import com.yondu.knowledgebase.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<RoleDTO> getAllRoles() {
        List<Role> roles = roleRepository.findAll();

        return roles.stream()
                .map(role -> new RoleDTO(role.getId(), role.getRoleName(), role.getUserPermissions(), role.getRolePagePermisisons()))
                .collect(Collectors.toList());
    }

    public RoleDTO getRole(Long id) {
        Optional<Role> optionalRole = roleRepository.findById(id);

        System.out.println(optionalRole);

        Role role = optionalRole.orElseThrow(() -> new NotFoundException("Role not found"));
        return new RoleDTO(role.getId(), role.getRoleName(), role.getUserPermissions(), role.getRolePagePermisisons());
    }

    public RoleDTO addRole(RoleDTO roleDTO) {
        // Check if a role with the same name already exists
        boolean roleExists = roleRepository.existsByRoleName(roleDTO.getRoleName());
        if (roleExists) {
            throw new BadRequestException("Role already exists");
        }

        // Save the role object directly
        Role role = new Role(roleDTO.getId(), roleDTO.getRoleName(), roleDTO.getPermission(), roleDTO.getRolePagePermissions());
        Role newRole = roleRepository.save(role);

        RoleDTO newRoleDTO = new RoleDTO(newRole.getId(), newRole.getRoleName(), newRole.getUserPermissions(), newRole.getRolePagePermisisons());

        // Return the same roleDTO object
        return newRoleDTO;
    }
}