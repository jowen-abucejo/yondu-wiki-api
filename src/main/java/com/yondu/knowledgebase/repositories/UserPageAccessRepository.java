package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface UserPageAccessRepository extends JpaRepository<UserPageAccess, Long> {

    @Query("SELECT DISTINCT pa FROM user_page_access pa WHERE pa.user = :user AND pa.page = :page AND pa.permission = :permission")
    boolean existsByPageAndUserAndPermission(Long page, Long user, Long permission);

    Set<UserPageAccess> findByUserAndPage(User user, Page page);

    @Query("SELECT DISTINCT pa FROM user_page_access pa WHERE pa.user = :id AND pa.page = :pageId AND pa.permission = :rightsId")
    Optional<UserPageAccess> findByPageAndUserAndPermission(Page pageId, User id, Permission rightsId);

    Set<UserPageAccess> findByUser(User user);

    Set<UserPageAccess> findByPage(Page page);

}
