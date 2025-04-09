package id.co.bcaf.adapinjam.repositories;

import id.co.bcaf.adapinjam.models.Pengajuan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PengajuanRepository extends JpaRepository<Pengajuan, UUID> {
    List<Pengajuan> findByStatusAndCustomerId(String status, UUID customerId);
    List<Pengajuan> findByCustomer_Id(UUID customerId);

}
