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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public Set<UserPagePermissionDTO.BaseResponse> addUserToPageAccess(Long pageId, UserPagePermissionDTO.AddUser userPermission) {

        Set<UserPagePermissionDTO.BaseResponse> setResponse = new HashSet<>();
        for(UserPagePermissionDTO.UserPermissionPair userPermissionPair : userPermission.userPermissionPair()){

            User user = userRepository.findById(userPermissionPair.userId()).orElseThrow(()-> new ResourceNotFoundException("User not found."));

            Permission permission = permissionRepository.findById(userPermissionPair.permissionId()).orElseThrow(()-> new ResourceNotFoundException("Permission not found."));

            Page page = pageRepository.findByIdAndActive(pageId, true).orElseThrow(()-> new ResourceNotFoundException("Page not found."));

            if(userPagePermissionRepository.findByPageAndPermissionAndUserAndIsActive(page, permission, user, true).orElse(null) == null){
                UserPagePermission savedUserPagePermission = userPagePermissionRepository.save(new UserPagePermission(permission, user, page, true, LocalDateTime.now(), LocalDateTime.now()));
                setResponse.add(UserPagePermissionDTOMapper.mapToBaseResponse(savedUserPagePermission));
            } else {
                throw new DuplicateResourceException("User "+user.getEmail()+" already has permission "+ permission.getName() +" on page " +page.getId());
            }

        }
        return setResponse;
    }

    @Override
    public UserPagePermissionDTO.BaseResponse removeUserToPageAccess(Long pageId, UserPagePermissionDTO.UserPermissionPair userPagePermission) {
        User user = userRepository.findById(userPagePermission.userId()).orElseThrow(()-> new ResourceNotFoundException("User not found."));

        Permission permission = permissionRepository.findById(userPagePermission.permissionId()).orElseThrow(()-> new ResourceNotFoundException("Permission not found."));

        Page page = pageRepository.findByIdAndActive(pageId, true).orElseThrow(()-> new ResourceNotFoundException("Page not found."));

        userPagePermissionRepository.modifyUserPermission(false, LocalDateTime.now(), page, permission ,user, true);

        UserPagePermission savedUserPagePermission = userPagePermissionRepository.findByPageAndPermissionAndUserAndIsActive( page, permission, user, false).orElseThrow(()-> new ResourceNotFoundException("User with this specific page permission is not found."));

        return UserPagePermissionDTOMapper.mapToBaseResponse(savedUserPagePermission);
    }

    /**
     * Retrieves all the pages the user has access to
     * **/
    @Override
    public List<UserPagePermissionDTO.BaseResponse> getAllPageOfUser(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found."));

        List<UserPagePermission> userPagePermission = userPagePermissionRepository.findAllByUserAndIsActiveGroupByPage(user, true);
        return userPagePermission.stream().map(UserPagePermissionDTOMapper::mapToBaseResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all the users with access on the page
     * **/
    @Override
    public List<UserPagePermissionDTO.BaseResponse> getAllUsersOfPage(Long pageId) {

        Page page = pageRepository.findById(pageId).orElseThrow(() -> new ResourceNotFoundException("Page not found."));

        List<UserPagePermission> userPagePermission = userPagePermissionRepository.findAllByPageAndIsActiveGroupByUser(page, true);



        return userPagePermission.stream().map(UserPagePermissionDTOMapper::mapToBaseResponse)
                .collect(Collectors.toList());
    }

}
