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

        ResponseEntity response = null;
        ApiResponse apiResponse = null;

        try{
             NotificationDTO.Base newNotification = notificationService.createNotification(notification);

            apiResponse = ApiResponse.success(newNotification, "success");
            response = ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        }catch (Exception ex){
            ex.printStackTrace();

            apiResponse = ApiResponse.error(ex.getMessage());
            response = ResponseEntity.internalServerError().body(apiResponse);
        }

        return response;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserNotifications(@PathVariable long userId,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "15") int size) {
        log.info("NotificationController.getUserNotifications()");
        log.info("userId : " + userId);
        log.info("page : " + page);
        log.info("size : " + size);

        ResponseEntity response = null;
        ApiResponse apiResponse = null;

        try{
            PaginatedResponse<NotificationDTO.Base> notifications = notificationService.getUserNotifications(userId, page, size);
            if(notifications.getTotal() <= 0){
                apiResponse = ApiResponse.success(notifications, "no content");
                response = ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
            }else{
                apiResponse = ApiResponse.success(notifications, "success");
                response = ResponseEntity.ok(apiResponse);
            }
        }catch (Exception ex) {
            ex.printStackTrace();

            apiResponse = ApiResponse.error(ex.getMessage());
            response = ResponseEntity.internalServerError().body(apiResponse);
        }

        return response;
    }
}
