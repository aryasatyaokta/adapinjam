package id.co.bcaf.adapinjam.repositories;

import id.co.bcaf.adapinjam.models.Feature;
import id.co.bcaf.adapinjam.models.RoleToFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import id.co.bcaf.adapinjam.models.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleToFeatureRepository extends JpaRepository<RoleToFeature, Integer> {

    boolean existsByRoleAndFeature(Role role, Feature feature);

    @Query("SELECT rf.feature FROM RoleToFeature rf WHERE rf.role.id = :idRole")
    List<Feature> findFeaturesByRoleId(@Param("idRole") int idRole);

    List<RoleToFeature> findByRoleId(Integer roleId);

    Optional<RoleToFeature> findByRoleIdAndFeatureId(Integer roleId, Integer featureId);

    void deleteByRole(Role role);

}

