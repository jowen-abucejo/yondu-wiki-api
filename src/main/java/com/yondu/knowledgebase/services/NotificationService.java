package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.notification.NotificationDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;

public interface NotificationService {

    public NotificationDTO.BaseResponse createNotification(NotificationDTO.BaseRequest notification);
    public PaginatedResponse<NotificationDTO.BaseResponse> getUserNotifications(int page, int size);
    public PaginatedResponse<NotificationDTO.BaseResponse> getUserUnreadNotifications(int page, int size); 
    public NotificationDTO.Base readNotification(long notificationId);
    public boolean readAllNotification();
    public NotificationDTO.TotalUnreadNotification getTotalUnreadNotification ();
}
