package id.co.bcaf.adapinjam.repositories;

import id.co.bcaf.adapinjam.models.Plafon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlafonRepository extends JpaRepository<Plafon, Integer> {
    List<Plafon> findByDeletedFalse();
//    Optional<Plafon> findByUserId(String userId);
}
