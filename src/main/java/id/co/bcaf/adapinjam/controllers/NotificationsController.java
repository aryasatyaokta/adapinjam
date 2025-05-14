package id.co.bcaf.adapinjam.controllers;

import id.co.bcaf.adapinjam.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationsController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public String sendNotification(
            @RequestParam String token,
            @RequestParam String title,
            @RequestParam String body) {

        return notificationService.sendNotification(token, title, body);
    }
}
