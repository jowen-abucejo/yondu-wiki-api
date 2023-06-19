package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Tag;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    public Set<Tag> findByNameIn(List<String> tags);
}
