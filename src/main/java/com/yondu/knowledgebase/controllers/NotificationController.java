package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.notification.NotificationDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.entities.Notification;
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
    public ResponseEntity<?> createNewNotification(@RequestBody NotificationDTO.Base notification) {
        log.info("NotificationController.createNewNotification()");
        log.info("notification : " + notification.toString());

        NotificationDTO.Base newNotification = notificationService.createNotification(notification);

        ApiResponse apiResponse = ApiResponse.success(newNotification, "success");
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserNotifications(@PathVariable long userId,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "15") int size) {
        log.info("NotificationController.getUserNotifications()");
        log.info("userId : " + userId);
        log.info("page : " + page);
        log.info("size : " + size);

        PaginatedResponse<NotificationDTO.Base> notifications = notificationService.getUserNotifications(userId, page, size);
        ApiResponse apiResponse = ApiResponse.success(notifications, "success");

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> readNotification(@PathVariable long id) {
        log.info("NotificationController.readNotification()");
        log.info("id : " + id);

        NotificationDTO.Base notification = notificationService.readNotification(id);
        return ResponseEntity.ok(notification);
    }
}
