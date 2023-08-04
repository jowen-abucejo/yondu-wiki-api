package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Save;
import com.yondu.knowledgebase.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SaveRepository extends JpaRepository<Save,Long> {

    Page<Save> findAllByAuthorOrderByDateCreatedDesc(User author, Pageable pageable);

    Page<Save> findAllByAuthorAndEntityTypeOrderByDateCreatedDesc(User author, String entityType, Pageable pageable);

    <Optional>Save findByEntityTypeAndEntityIdAndAuthor(String entity, Long id, User author);

    List<Save> findByAuthor(User author);

}
