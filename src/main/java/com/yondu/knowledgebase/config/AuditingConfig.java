package com.yondu.knowledgebase.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.services.UserService;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditingConfig {
    @Autowired
    private UserService userService;

    @Bean
    AuditorAware<User> auditorProvider() {
        return new AuditorAware<User>() {
            @Override
            public Optional<User> getCurrentAuditor() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null || !authentication.isAuthenticated()) {
                    return null;
                }

                return Optional.of(userService.loadUserByUsername(authentication.getName()));
            }

        };
    }
}
