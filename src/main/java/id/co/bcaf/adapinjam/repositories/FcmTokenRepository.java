package id.co.bcaf.adapinjam.repositories;

import id.co.bcaf.adapinjam.models.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FcmTokenRepository extends JpaRepository<FcmToken, UUID> {
    Optional<FcmToken> findByUser_Email(String email);
    Optional<FcmToken> findByToken(String token);
}
