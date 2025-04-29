package id.co.bcaf.adapinjam.services;

import id.co.bcaf.adapinjam.dtos.CreateRoleRequest;
import id.co.bcaf.adapinjam.models.Feature;
import id.co.bcaf.adapinjam.models.Role;
import id.co.bcaf.adapinjam.models.RoleToFeature;
import id.co.bcaf.adapinjam.repositories.FeatureRepository;
import id.co.bcaf.adapinjam.repositories.RoleRepository;
import id.co.bcaf.adapinjam.repositories.RoleToFeatureRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleToFeatureService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private RoleToFeatureRepository roleToFeatureRepository;

    // Mengambil fitur berdasarkan role
    public List<Feature> getFeaturesByRole(Integer roleId) {
        return roleToFeatureRepository.findFeaturesByRoleId(roleId);
    }

    public void addRoleWithFeatures(CreateRoleRequest createRoleRequest) {
        // Membuat role baru
        Role role = new Role();
        role.setNameRole(createRoleRequest.getName());
        Role savedRole = roleRepository.save(role);

        // Menambahkan fitur-fitur ke role baru
        if (createRoleRequest.getFeatureIds() != null) {
            for (Long featureId : createRoleRequest.getFeatureIds()) {
                Feature feature = featureRepository.findById(Math.toIntExact(featureId))
                        .orElseThrow(() -> new RuntimeException("Feature not found: " + featureId));
                RoleToFeature roleToFeature = new RoleToFeature();
                roleToFeature.setRole(savedRole);
                roleToFeature.setFeature(feature);
                roleToFeatureRepository.save(roleToFeature);
            }
        }
    }

    @Transactional
    public void updateRoleWithFeatures(Integer roleId, CreateRoleRequest createRoleRequest) {
        // Ambil role dari DB
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));

        // Update nama role
        role.setNameRole(createRoleRequest.getName());
        roleRepository.save(role);

        // Hapus semua fitur lama yang terkait dengan role
        roleToFeatureRepository.deleteByRole(role);

        // Tambahkan fitur baru dari request
        if (createRoleRequest.getFeatureIds() != null && !createRoleRequest.getFeatureIds().isEmpty()) {
            List<RoleToFeature> newRoleToFeatures = createRoleRequest.getFeatureIds().stream()
                    .map(featureId -> {
                        Feature feature = featureRepository.findById(Math.toIntExact(featureId))
                                .orElseThrow(() -> new RuntimeException("Feature not found: " + featureId));
                        RoleToFeature roleToFeature = new RoleToFeature();
                        roleToFeature.setRole(role);
                        roleToFeature.setFeature(feature);
                        return roleToFeature;
                    }).toList();

            roleToFeatureRepository.saveAll(newRoleToFeatures);
        }
    }

}
