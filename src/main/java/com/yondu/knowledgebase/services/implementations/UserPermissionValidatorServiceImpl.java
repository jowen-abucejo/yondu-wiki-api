package com.yondu.knowledgebase.services.implementations;

import org.springframework.stereotype.Service;

import com.yondu.knowledgebase.repositories.UserRepository;
import com.yondu.knowledgebase.services.UserPermissionValidatorService;

@Service
public class UserPermissionValidatorServiceImpl implements UserPermissionValidatorService {

    private final UserRepository userRepository;

    /**
     * @param userRepository
     */
    public UserPermissionValidatorServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Boolean userHasPagePermission(Long userId, Long pageId, String permission) {
        return userRepository.userHasPagePermission(userId, pageId, permission) > 0;
    }

    @Override
    public Boolean userHasDirectoryPermission(Long userId, Long directoryId, String permission) {
        return userRepository.userHasDirectoryPermission(userId, directoryId, permission) > 0;
    }

}
