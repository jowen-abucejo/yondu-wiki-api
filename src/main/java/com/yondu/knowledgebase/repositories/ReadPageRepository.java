package com.yondu.knowledgebase.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.yondu.knowledgebase.entities.ReadPage;
import com.yondu.knowledgebase.entities.compositekeys.ReadPageId;

public interface ReadPageRepository extends JpaRepository<ReadPage, ReadPageId> {

    @Query("SELECT page.id from ReadPage WHERE user.id=:userId AND page.type=:pageType")
    public Set<Long> findAllPageIdByUserIdAndPageType(Long userId, String pageType);
}
