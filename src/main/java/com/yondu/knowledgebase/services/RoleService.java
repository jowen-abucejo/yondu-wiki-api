package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.role.RoleDTO;
import com.yondu.knowledgebase.entities.Permission;
import com.yondu.knowledgebase.entities.Role;
import com.yondu.knowledgebase.exceptions.RequestValidationException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.PermissionRepository;
import com.yondu.knowledgebase.repositories.RoleRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    private final PermissionRepository permissionRepository;

    private final UserRepository userRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
    }

    public List<RoleDTO> getAllRoles() {
        List<Role> roles = roleRepository.findAll();

        return roles.stream()
                .map(RoleDTO::new)
                .collect(Collectors.toList());
    }

    public RoleDTO getRole(Long id) {
        Optional<Role> optionalRole = roleRepository.findById(id);

        Role role = optionalRole.orElseThrow(() -> new ResourceNotFoundException("Role with id: " + id + " not found"));
        return new RoleDTO(role);
    }

    public RoleDTO addRole(RoleDTO roleDTO) {
        // Check if a role with the same name already exists
        boolean roleExists = roleRepository.existsByRoleName(roleDTO.getRoleName());
        if (roleExists) {
            throw new RequestValidationException("Role already exists");
        }

        Set<Permission> permissions = roleDTO.getPermission().stream()
                .map(permissionDTO -> permissionRepository.findById(permissionDTO.id()).orElse(null))
                .collect(Collectors.toSet());

        // Save the role object directly
        Role role = new Role(roleDTO.getId(), roleDTO.getRoleName(), permissions);
        Role newRole = roleRepository.save(role);


        // Return the same roleDTO object
        return new RoleDTO(newRole);
    }

    public RoleDTO editRoleById(Long id, RoleDTO roleDTO) {

        Optional<Role> optionalRole = roleRepository.findById(id);
        Role role = optionalRole.orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        role.setRoleName(roleDTO.getRoleName());

        Set<Permission> permissions = roleDTO.getPermission().stream()
                .map(permissionDTO -> permissionRepository.findById(permissionDTO.id()).orElse(null))
                .collect(Collectors.toSet());

        role.setPermissions(permissions);
        Role newRole = roleRepository.save(role);

        return new RoleDTO(newRole);

    }

    public void deleteRoleById(Long id) {
        Optional<Role> optionalRole = roleRepository.findById(id);

        if (optionalRole.isEmpty()) {
            throw new ResourceNotFoundException("Role not found");
        }

        // Perform any additional validations or checks before deleting the role

        // For example, check if the role is associated with any users
        boolean isRoleAssociatedWithUsers = userRepository.findAll().stream()
                .anyMatch(user -> user.getRole().stream().anyMatch(roleMap -> roleMap.getId().equals(id)));

        if (isRoleAssociatedWithUsers) {
            throw new RequestValidationException("Cannot delete role. It is associated with users.");
        }

        // If all validations pass, proceed with deleting the role
        roleRepository.deleteById(id);
    }
}