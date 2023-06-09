package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.PasswordChanges;
import com.yondu.knowledgebase.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordChangesRepository extends JpaRepository<PasswordChanges, Long> {

    @Query("SELECT p FROM PasswordChanges p WHERE user = ?1")
    List<PasswordChanges> findByUser(User user);
}
