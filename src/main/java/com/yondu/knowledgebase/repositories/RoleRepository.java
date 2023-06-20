package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByRoleName(String roleName);

    Role findByRoleName(String roleName);

    Page<Role> findByRoleNameStartingWithIgnoreCase(String searchKey, Pageable pageable);
}
