package id.co.bcaf.adapinjam.repositories;

import id.co.bcaf.adapinjam.models.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Integer> {
    Optional<Feature> findByName(String name);
}
