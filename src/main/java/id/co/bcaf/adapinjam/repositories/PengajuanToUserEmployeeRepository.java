package id.co.bcaf.adapinjam.repositories;

import id.co.bcaf.adapinjam.models.PengajuanToUserEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PengajuanToUserEmployeeRepository extends JpaRepository<PengajuanToUserEmployee, UUID> {
    Optional<PengajuanToUserEmployee> findByPengajuanIdAndUserEmployeeId(UUID pengajuanId, UUID employeeId);
    List<PengajuanToUserEmployee> findByUserEmployeeIdAndPengajuanStatus(UUID employeeId, String status);
    List<PengajuanToUserEmployee> findByPengajuanId(UUID pengajuanId);
    List<PengajuanToUserEmployee> findByUserEmployeeId(UUID employeeId);
    List<PengajuanToUserEmployee> findByUserEmployee_Id(UUID userEmployeeId);
}
