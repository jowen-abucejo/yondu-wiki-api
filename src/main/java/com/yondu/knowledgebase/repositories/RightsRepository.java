package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Rights;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface RightsRepository extends JpaRepository<Rights, Long> {
}
