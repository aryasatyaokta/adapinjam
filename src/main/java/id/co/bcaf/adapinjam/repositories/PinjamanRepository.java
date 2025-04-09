package id.co.bcaf.adapinjam.repositories;

import id.co.bcaf.adapinjam.models.Pinjaman;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PinjamanRepository extends JpaRepository<Pinjaman, UUID> {
    List<Pinjaman> findByCustomer_Id(UUID customerId);
    List<Pinjaman> findByCustomer_IdAndLunasTrue(UUID customerId);
}


