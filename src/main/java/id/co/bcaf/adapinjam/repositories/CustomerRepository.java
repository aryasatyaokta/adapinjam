package id.co.bcaf.adapinjam.repositories;

import id.co.bcaf.adapinjam.models.UserCustomer;
import id.co.bcaf.adapinjam.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<UserCustomer, UUID> {
    Optional<UserCustomer> findByUser(User user);
    boolean existsByUser(User user);
    Optional<UserCustomer> findByUserEmail(String email);
}
