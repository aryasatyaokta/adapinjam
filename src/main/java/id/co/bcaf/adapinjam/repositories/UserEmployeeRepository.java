package id.co.bcaf.adapinjam.repositories;

import id.co.bcaf.adapinjam.models.UserEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserEmployeeRepository extends JpaRepository<UserEmployee, UUID> {
    Optional<UserEmployee> findByUserEmail(String email);
    Optional<UserEmployee> findById(UUID id);
    Optional<UserEmployee> findByNip(String nip);
    Optional<UserEmployee> findByUserId(UUID userId);
    @Query("SELECT u FROM UserEmployee u WHERE u.branch.id = :branchId AND u.user.role.id = :roleId")
    List<UserEmployee> findByBranchIdAndUserRole(UUID branchId, int roleId);
    List<UserEmployee> findByBranchIdAndStatusEmployee(UUID branchId, String statusEmployee);
//
//    @Query("SELECT ue FROM UserEmployee ue JOIN PengajuanUserEmployee pue ON ue.id = pue.userEmployee.id WHERE pue.pengajuan.id = :pengajuanId")
//    static List<UserEmployee> findByPengajuanId(@Param("pengajuanId") UUID pengajuanId) {
//        return null;
//    }

    @Query("""
    SELECT e FROM UserEmployee e
    WHERE e.branch.id = :branchId 
      AND e.user.role.id = 2
      AND e.statusEmployee = 'ACTIVE'
    ORDER BY (
        SELECT COUNT(p.id) FROM PengajuanToUserEmployee p WHERE p.userEmployee.id = e.id
    ), e.user.name ASC
    LIMIT 1
""")
    UserEmployee findRandomMarketingByBranch(@Param("branchId") UUID branchId);


    List<UserEmployee> findByBranchIdAndUserRoleId(UUID branchId, Integer roleId);


}
