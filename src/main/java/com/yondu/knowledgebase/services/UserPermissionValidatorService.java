package com.yondu.knowledgebase.services;

public interface UserPermissionValidatorService {
    public Boolean userHasPagePermission(Long userId, Long pageId, String permission);

    public Boolean userHasDirectoryPermission(Long userId, Long directoryId, String permission);

    public Boolean currentUserIsSuperAdmin();
}
