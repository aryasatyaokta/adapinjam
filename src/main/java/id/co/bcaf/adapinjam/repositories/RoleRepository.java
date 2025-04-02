package id.co.bcaf.adapinjam.repositories;

import id.co.bcaf.adapinjam.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findById(Integer id);
}