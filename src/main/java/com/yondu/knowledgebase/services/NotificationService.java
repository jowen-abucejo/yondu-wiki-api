package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.notification.NotificationDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;

public interface NotificationService {

    public NotificationDTO.Base createNotification(NotificationDTO.Base notification) throws Exception;
    public PaginatedResponse<NotificationDTO.Base> getUserNotifications(long userId, int page, int size) throws Exception;
}
