package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<TestEntity, Long> {
}
