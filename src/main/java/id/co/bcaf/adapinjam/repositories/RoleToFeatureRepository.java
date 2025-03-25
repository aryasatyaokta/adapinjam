package id.co.bcaf.adapinjam.repositories;

import id.co.bcaf.adapinjam.models.RoleToFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleToFeatureRepository extends JpaRepository<RoleToFeature, Integer> {
}
