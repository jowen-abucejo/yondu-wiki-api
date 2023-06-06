package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Save;
import com.yondu.knowledgebase.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaveRepository extends JpaRepository<Save,Long> {

    List<Save> findAllByAuthor(User author);
}
