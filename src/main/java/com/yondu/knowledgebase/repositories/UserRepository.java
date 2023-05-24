package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
