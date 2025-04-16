package id.co.bcaf.adapinjam.controllers;

import id.co.bcaf.adapinjam.models.Notification;
import id.co.bcaf.adapinjam.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    // Lihat notifikasi yang belum dibaca
    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications() {
        return ResponseEntity.ok(notificationRepository.findByReadFalse());
    }

    // Tandai notifikasi sebagai sudah dibaca
    @PostMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@PathVariable UUID id) {
        return notificationRepository.findById(id).map(notif -> {
            notif.setRead(true);
            notificationRepository.save(notif);
            return ResponseEntity.ok("Notification marked as read");
        }).orElse(ResponseEntity.notFound().build());
    }

    // Hapus notifikasi
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable UUID id) {
        if (!notificationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        notificationRepository.deleteById(id);
        return ResponseEntity.ok("Notification deleted");
    }
}
