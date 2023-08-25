package com.yondu.knowledgebase.repositories;


import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.entities.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserOtpRepository extends JpaRepository<UserOtp, Long> {

    boolean existsByUser(User user);
    UserOtp findByUser (User user);

    boolean existsByOtpAndIsValidIsTrueAndExpirationDateAfter(String otp, LocalDateTime expirationDate);

    List<UserOtp> findByIsValidIsTrueAndExpirationDateAfter(LocalDateTime expirationDate);
}
