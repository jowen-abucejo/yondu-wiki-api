package com.yondu.knowledgebase.services;

import java.util.Map;

public interface UserPermissionValidatorService {
    public Boolean userHasPagePermission(Long userId, Long pageId, String permission);

    public Boolean userHasDirectoryPermission(Long userId, Long directoryId, String permission);

    public Boolean currentUserIsSuperAdmin();

    public Map<String, Long> checkUserPageApprovalPermissionAndGetNextWorkflowStep(Long userId, Long pageId,
            Long versionId);
}
