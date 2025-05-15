package id.co.bcaf.adapinjam.securitys;

import id.co.bcaf.adapinjam.models.Feature;
import id.co.bcaf.adapinjam.models.Role;
import id.co.bcaf.adapinjam.models.RoleToFeature;
import id.co.bcaf.adapinjam.repositories.FeatureRepository;
import id.co.bcaf.adapinjam.repositories.RoleToFeatureRepository;
import id.co.bcaf.adapinjam.repositories.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class InitConfig {

    @Bean
    @Transactional
    public CommandLineRunner initRolesAndFeatures(RoleRepository roleRepository,
                                                  FeatureRepository featureRepository,
                                                  RoleToFeatureRepository roleToFeatureRepository) {
        return args -> {
            // Inisialisasi Roles
            List<String> roles = List.of("Super Admin", "Marketing", "Branch Manager", "Back Office","Customer");
            Map<String, Role> roleMap = new HashMap<>();

            for (String roleName : roles) {
                Role role = roleRepository.findByNameRole(roleName).orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setNameRole(roleName);
                    return roleRepository.save(newRole);
                });
                roleMap.put(roleName, role);
            }

            // Mapping Feature ke Role yang relevan
            Map<String, List<String>> featureToRoles = new HashMap<>();
            featureToRoles.put("GET_ALL_BRANCH", List.of("Super Admin"));
            featureToRoles.put("GET_BRANCH_BY_ID", List.of("Super Admin"));
            featureToRoles.put("CREATE_BRANCH", List.of("Super Admin"));
            featureToRoles.put("UPDATE_BRANCH", List.of("Super Admin"));
            featureToRoles.put("GET_ALL_CUSTOMER", List.of("Super Admin"));
            featureToRoles.put("GET_CUSTOMER_BY_ID", List.of("Super Admin"));
            featureToRoles.put("CREATE_PENGAJUAN", List.of("Customer"));
            featureToRoles.put("REVIEW_PENGAJUAN", List.of("Marketing", "Branch Manager", "Back Office"));
            featureToRoles.put("REVIEW_HISTORY", List.of("Marketing", "Branch Manager", "Back Office"));
            featureToRoles.put("REVIEW_HISTORY_BY_ID", List.of("Marketing", "Branch Manager", "Back Office"));
            featureToRoles.put("CREATE_PLAFON", List.of("Super Admin"));
            featureToRoles.put("GET_ALL_PLAFON", List.of("Super Admin"));
            featureToRoles.put("GET_PLAFON_BY_ID", List.of("Super Admin"));
            featureToRoles.put("UPDATE_PLAFON", List.of("Super Admin"));
            featureToRoles.put("DELETE_PLAFON", List.of("Super Admin"));
            featureToRoles.put("GET_EMPLOYEE_BY_ID", List.of("Marketing", "Branch Manager", "Back Office"));
            featureToRoles.put("CREATE_EMPLOYEE", List.of("Super Admin"));
            featureToRoles.put("GET_ALL_EMPLOYEE", List.of("Super Admin"));
            featureToRoles.put("UPDATE_EMPLOYEE", List.of("Super Admin"));
            featureToRoles.put("GET_ROLES_FEATURES", List.of("Super Admin"));
            featureToRoles.put("CREATE_ROLES_FEATURES", List.of("Super Admin"));
            featureToRoles.put("UPDATE_ROLES_FEATURES", List.of("Super Admin"));
            featureToRoles.put("GET_ALL_ROLES", List.of("Super Admin"));
            // Inisialisasi fitur dan relasi ke role
            for (Map.Entry<String, List<String>> entry : featureToRoles.entrySet()) {
                String featureName = entry.getKey();

                // Cek & simpan feature jika belum ada
                Feature feature = featureRepository.findByName(featureName)
                        .orElseGet(() -> featureRepository.save(new Feature(0, featureName)));

                for (String roleName : entry.getValue()) {
                    Role role = roleMap.get(roleName);
                    boolean exists = roleToFeatureRepository.existsByRoleAndFeature(role, feature);
                    if (!exists) {
                        RoleToFeature roleToFeature = new RoleToFeature(0, role, feature);
                        roleToFeatureRepository.save(roleToFeature);
                        System.out.println("✅ Fitur " + featureName + " diberikan ke role " + roleName);
                    }
                }
            }

            System.out.println("🎉 Semua Role dan Feature berhasil diinisialisasi!");
        };
    }
}
