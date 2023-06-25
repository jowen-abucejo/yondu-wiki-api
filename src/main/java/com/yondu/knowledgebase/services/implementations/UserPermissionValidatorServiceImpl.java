package com.yondu.knowledgebase.services.implementations;

import java.util.Map;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

import com.yondu.knowledgebase.entities.Role;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.repositories.UserRepository;
import com.yondu.knowledgebase.services.UserPermissionValidatorService;

@Service
public class UserPermissionValidatorServiceImpl implements UserPermissionValidatorService {

    private final UserRepository userRepository;
    private final AuditorAware<User> auditorAware;

    /**
     * @param userRepository
     */
    public UserPermissionValidatorServiceImpl(UserRepository userRepository, AuditorAware<User> auditorAware) {
        this.userRepository = userRepository;
        this.auditorAware = auditorAware;
    }

    @Override
    public Boolean userHasPagePermission(Long userId, Long pageId, String permission) {
        return userRepository.userHasPagePermission(userId, pageId, permission) > 0;
    }

    @Override
    public Boolean userHasDirectoryPermission(Long userId, Long directoryId, String permission) {
        return userRepository.userHasDirectoryPermission(userId, directoryId, permission) > 0;
    }

    @Override
    public Boolean currentUserIsSuperAdmin() {
        User user = (User) this.auditorAware.getCurrentAuditor().orElse(new User());
        for (Role userRole : user.getRole()) {
            if (userRole.getRoleName().equals("Administrator")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<String, Long> checkUserPageApprovalPermissionAndGetNextWorkflowStep(Long userId, Long pageId,
            Long versionId) {
        return userRepository.checkUserPageApprovalPermissionAndGetNextWorkflowStep(userId, pageId,
                versionId);
    }

}
