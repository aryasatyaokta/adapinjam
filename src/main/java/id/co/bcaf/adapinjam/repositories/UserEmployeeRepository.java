package id.co.bcaf.adapinjam.repositories;

import id.co.bcaf.adapinjam.models.UserEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserEmployeeRepository extends JpaRepository<UserEmployee, UUID> {
    Optional<UserEmployee> findById(UUID id);
    Optional<UserEmployee> findByNip(String nip);
}
