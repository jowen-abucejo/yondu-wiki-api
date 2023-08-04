package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Directory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DirectoryRepository extends JpaRepository<Directory, Long> {
    Optional<Directory> findByNameAndParent(String name, Directory parent);

    // TODO: Custom query to retrieve full path as string
    // @Query(nativeQuery = true, value = """

    // """)
    Optional<Page<Directory>> findByDirectoryUserAccessesPermissionNameAndDirectoryUserAccessesUserId(String permission,
            Long userId, Pageable paging);
}
