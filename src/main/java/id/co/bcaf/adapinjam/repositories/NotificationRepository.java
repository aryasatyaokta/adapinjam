package id.co.bcaf.adapinjam.repositories;

import id.co.bcaf.adapinjam.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByReadFalse();
}
