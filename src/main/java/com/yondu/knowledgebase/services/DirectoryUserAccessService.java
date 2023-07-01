package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.directory.user_access.DirectoryUserAccessDTO;
import org.springframework.stereotype.Service;

@Service
public interface DirectoryUserAccessService {
    public DirectoryUserAccessDTO.UserAccessResult updateUserAccess(Long directoryId, DirectoryUserAccessDTO.UserAccess userAccess);
}
