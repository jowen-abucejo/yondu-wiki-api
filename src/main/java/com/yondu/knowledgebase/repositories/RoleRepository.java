package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByRoleName(String roleName);

    Role findByRoleName(String roleName);

    Page<Role> findByRoleNameStartingWithIgnoreCase(String searchKey, Pageable pageable);

    @Query("SELECT r FROM Role r WHERE r.roleName LIKE %:searchKey%")
    Page<Role> findByRoleNameKey(String searchKey, Pageable pageable);

    @Query(nativeQuery = true, value = """
               SELECT CASE WHEN EXISTS (
                    select 1 from user_role where role_id = :roleId
               ) THEN TRUE ELSE FALSE END
            """)
    Long checkIfRoleHasUser(Long roleId);

}
