package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.DTO.directory.user_access.DirectoryUserAccessDTO;
import com.yondu.knowledgebase.entities.Directory;
import com.yondu.knowledgebase.entities.DirectoryUserAccess;
import com.yondu.knowledgebase.entities.Permission;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.DirectoryRepository;
import com.yondu.knowledgebase.repositories.DirectoryUserAccessRepository;
import com.yondu.knowledgebase.repositories.PermissionRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import com.yondu.knowledgebase.services.DirectoryUserAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DirectoryUserAccessServiceImpl implements DirectoryUserAccessService {

    private Logger log = LoggerFactory.getLogger(DirectoryUserAccessServiceImpl.class);
    @Autowired
    private DirectoryUserAccessRepository directoryUserAccessRepository;
    @Autowired
    private DirectoryRepository directoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    public DirectoryUserAccessDTO.UserAccessResult updateUserAccess(Long directoryId, DirectoryUserAccessDTO.UserAccess userAccess) {
        log.info("DirectoryUserAccess.updateUserAccess()");
        log.info("directoryId : " + directoryId);
        log.info("userAccess  : " + userAccess);

        Directory dir = directoryRepository.findById(directoryId).orElseThrow(() -> new ResourceNotFoundException("The directory cannot be found."));
        User user = userRepository.findByEmail(userAccess.email()).orElseThrow(() -> new ResourceNotFoundException("Cannot find this user."));
        Permission permission = permissionRepository.findById(userAccess.permission_id()).orElseThrow(() -> new ResourceNotFoundException("Cannot find the permission."));

        DirectoryUserAccess exist = directoryUserAccessRepository.findByDirectoryAndPermissionAndUser(dir, permission, user).orElse(null);
        // If the permission already exists. It will be deleted.
        if(exist == null){
            DirectoryUserAccess newDirectoryUserAccess = new DirectoryUserAccess(dir, permission, user);

            directoryUserAccessRepository.save(newDirectoryUserAccess);
            var res = new DirectoryUserAccessDTO.UserAccess(user.getEmail(), permission.getId());
            return new DirectoryUserAccessDTO.UserAccessResult(res, "CREATED");
        }else{
            directoryUserAccessRepository.delete(exist);
            var res = new DirectoryUserAccessDTO.UserAccess(user.getEmail(), permission.getId());
            return new DirectoryUserAccessDTO.UserAccessResult(res, "REMOVED");
        }
    }
}
