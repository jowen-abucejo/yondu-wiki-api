package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Directory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DirectoryRepository extends JpaRepository<Directory, Long> {
}
