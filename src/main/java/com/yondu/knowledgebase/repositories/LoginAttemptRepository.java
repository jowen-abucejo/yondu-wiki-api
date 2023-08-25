package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.LoginAttempt;
import com.yondu.knowledgebase.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    boolean existsByUser(User user);
    LoginAttempt findByUser (User user);
}
