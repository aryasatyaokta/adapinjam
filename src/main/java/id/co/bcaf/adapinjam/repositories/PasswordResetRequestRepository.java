package id.co.bcaf.adapinjam.repositories;

import id.co.bcaf.adapinjam.models.PasswordResetRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PasswordResetRequestRepository extends JpaRepository<PasswordResetRequest, UUID> {
    List<PasswordResetRequest> findByProcessedFalse();
}
