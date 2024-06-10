package com.example.oauth2.notify;


import com.example.oauth2.payload.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WebSocketController {

    @Autowired
    private NotificationService notificationService;


    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse> getNotifications() {
        List<Notify> notifications = notificationService.getNotificationsByUsername();
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Thành công", notifications));
    }


}
