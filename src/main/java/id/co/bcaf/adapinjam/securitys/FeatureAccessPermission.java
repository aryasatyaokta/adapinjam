package id.co.bcaf.adapinjam.securitys;

import id.co.bcaf.adapinjam.models.Feature;
import id.co.bcaf.adapinjam.models.User;
import id.co.bcaf.adapinjam.repositories.RoleToFeatureRepository;
import id.co.bcaf.adapinjam.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component("accessPermission")
public class FeatureAccessPermission {
    private final UserRepository userRepository;
    private final RoleToFeatureRepository roleToFeatureRepository;

    @Autowired
    public FeatureAccessPermission(UserRepository userRepository, RoleToFeatureRepository roleToFeatureRepository) {
        this.userRepository = userRepository;
        this.roleToFeatureRepository = roleToFeatureRepository;
    }

    public boolean hasAccess(Authentication authentication, String featureName) {
        String email = authentication.getName();

        Optional<User> idUser = userRepository.findByEmail(email);
        // dapat dari JWT
        User user = userRepository.findById(idUser.get().getId()).orElse(null);
        if (user == null || user.getRole() == null) return false;

        List<Feature> allowedFeatures = roleToFeatureRepository.findFeaturesByRoleId(user.getRole().getId());

        return allowedFeatures.stream()
                .anyMatch(f -> f.getName_feature().equalsIgnoreCase(featureName));
    }
}
