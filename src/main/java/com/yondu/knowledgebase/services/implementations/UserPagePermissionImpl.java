package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.DTO.page_permission.user_access.UserPagePermissionDTO;
import com.yondu.knowledgebase.DTO.page_permission.user_access.UserPagePermissionDTOMapper;
import com.yondu.knowledgebase.entities.*;
import com.yondu.knowledgebase.exceptions.DuplicateResourceException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.PageRepository;
import com.yondu.knowledgebase.repositories.PermissionRepository;
import com.yondu.knowledgebase.repositories.UserPagePermissionRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import com.yondu.knowledgebase.services.UserPagePermissionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserPagePermissionImpl implements UserPagePermissionService {

    private final UserPagePermissionRepository userPagePermissionRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PageRepository pageRepository;

    public UserPagePermissionImpl (UserPagePermissionRepository userPagePermissionRepository, UserRepository userRepository,
                                   PermissionRepository permissionRepository, PageRepository pageRepository){
        this.userPagePermissionRepository = userPagePermissionRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.pageRepository = pageRepository;
    }

    @Override
    public UserPagePermissionDTO.BaseResponse addUserToPageAccess(Long permissionId, UserPagePermissionDTO.AddUser userPagePermission) {

        User user = userRepository.findById(userPagePermission.userId()).orElseThrow(()-> new ResourceNotFoundException("User not found."));

        Permission permission = permissionRepository.findById(permissionId).orElseThrow(()-> new ResourceNotFoundException("Permission not found."));

        Page page = pageRepository.findByIdAndActive(userPagePermission.pageId(), true).orElseThrow(()-> new ResourceNotFoundException("Page not found."));

        if(userPagePermissionRepository.findByPageAndPermissionAndUserAndIsActive(page, permission, user, true).orElse(null) == null){
            UserPagePermission savedUserPagePermission = userPagePermissionRepository.save(new UserPagePermission(permission, user, page, true, LocalDateTime.now(), LocalDateTime.now()));
            return UserPagePermissionDTOMapper.mapToBaseResponse(savedUserPagePermission);
        } else {
            throw new DuplicateResourceException("User "+user.getEmail()+" already has this permission on page " +page.getId());
        }

    }

    @Override
    public UserPagePermissionDTO.BaseResponse removeUserToPageAccess(Long permissionId, UserPagePermissionDTO.AddUser userPagePermission) {
        User user = userRepository.findById(userPagePermission.userId()).orElseThrow(()-> new ResourceNotFoundException("User not found."));

        Permission permission = permissionRepository.findById(permissionId).orElseThrow(()-> new ResourceNotFoundException("Permission not found."));

        Page page = pageRepository.findByIdAndActive(userPagePermission.pageId(), true).orElseThrow(()-> new ResourceNotFoundException("Page not found."));

        userPagePermissionRepository.modifyUserPermission(false, LocalDateTime.now(), page, permission ,user, true);

        UserPagePermission savedUserPagePermission = userPagePermissionRepository.findByPageAndPermissionAndUserAndIsActive( page, permission, user, false).orElseThrow(()-> new ResourceNotFoundException("User with this specific page permission is not found."));

        return UserPagePermissionDTOMapper.mapToBaseResponse(savedUserPagePermission);

    }

    @Override
    public List<UserPagePermissionDTO.BaseResponse> getAllPagePermissionOfUser(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found."));

        List<UserPagePermission> userPagePermission = userPagePermissionRepository.findAllByUserAndIsActive(user, true);
        return userPagePermission.stream().map(UserPagePermissionDTOMapper::mapToBaseResponse)
                .collect(Collectors.toList());
    }

}
