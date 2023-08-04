package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.notification.NotificationDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    private final Logger log = LoggerFactory.getLogger(NotificationController.class);

    @PostMapping("")
    public ResponseEntity<ApiResponse<NotificationDTO.BaseResponse>> createNewNotification(@RequestBody NotificationDTO.BaseRequest notification) {
        log.info("NotificationController.createNewNotification()");
        log.info("notification : " + notification.toString());

        NotificationDTO.BaseResponse newNotification = notificationService.createNotification(notification);

        ApiResponse apiResponse = ApiResponse.success(newNotification, "success");
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<PaginatedResponse<NotificationDTO.BaseResponse>>> getUserNotifications(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "15") int size) {
        log.info("NotificationController.getUserNotifications()");
        log.info("page : " + page);
        log.info("size : " + size);

        PaginatedResponse<NotificationDTO.BaseResponse> notifications = notificationService.getUserNotifications(page, size);
        ApiResponse apiResponse = ApiResponse.success(notifications, "success");

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationDTO.Base>> readNotification(@PathVariable long id) {
        log.info("NotificationController.readNotification()");
        log.info("id : " + id);

        NotificationDTO.Base notification = notificationService.readNotification(id);
        ApiResponse response = ApiResponse.success(notification, "success");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/readAll")
    public ResponseEntity<ApiResponse<PaginatedResponse<NotificationDTO.Base>>> readAllNotification() {
        log.info("NotificationController.readAllNotification()");

        notificationService.readAllNotification();

        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/total-unread")
    public ResponseEntity<ApiResponse<Long>> totalUnreadNotification () {
        NotificationDTO.TotalUnreadNotification totalUnreadNotification = notificationService.getTotalUnreadNotification();
        ApiResponse response = ApiResponse.success(totalUnreadNotification, "total unread notifications fetched successfully");
        return ResponseEntity.ok(response);
    }
 
    @GetMapping("/user/unread")
    public ResponseEntity<ApiResponse<PaginatedResponse<NotificationDTO.BaseResponse>>> getUserUnreadNotif(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "15") int size) {
        log.info("NotificationController.getUserNotifications()");
        log.info("page : " + page);
        log.info("size : " + size);

        PaginatedResponse<NotificationDTO.BaseResponse> unreadNotifications = notificationService.getUserUnreadNotifications(page, size);
        ApiResponse apiResponse = ApiResponse.success(unreadNotifications, "success");

        return ResponseEntity.ok(apiResponse);
    }
    
}
