package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM users u WHERE email = ?1 AND status = 'ACT'")
    User getUserByEmail(String email);
}
