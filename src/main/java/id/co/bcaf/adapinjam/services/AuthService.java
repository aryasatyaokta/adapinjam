package id.co.bcaf.adapinjam.services;

import id.co.bcaf.adapinjam.dtos.RegisterRequest;
import id.co.bcaf.adapinjam.models.Role;
import id.co.bcaf.adapinjam.models.User;
import id.co.bcaf.adapinjam.models.UserEmployee;
import id.co.bcaf.adapinjam.repositories.UserEmployeeRepository;
import id.co.bcaf.adapinjam.repositories.UserRepository;
import id.co.bcaf.adapinjam.utils.JwtUtil;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserEmployeeRepository userEmployeeRepository;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, UserEmployeeRepository userEmployeeRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userEmployeeRepository = userEmployeeRepository;
    }

    public String authenticateUser(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(password, user.getPassword())) { // Hash password checking
                logger.info("User {} authenticated, generating token...", email);
                return jwtUtil.generateToken(email, user.getRole().getName_role());
            }
        }

        logger.warn("Invalid login attempt for email: {}", email);
        return null;
    }

    public String authenticateUserEmployee(String nip, String password) {
        Optional<UserEmployee> optionalUserEmployee = userEmployeeRepository.findByNip(nip);

        if (optionalUserEmployee.isPresent()) {
            UserEmployee userEmployee = optionalUserEmployee.get();
            if (passwordEncoder.matches(password, userEmployee.getUser().getPassword())) {
                return jwtUtil.generateToken(userEmployee.getUser().getEmail(), userEmployee.getUser().getRole().getName_role());
            }
        }
        return null;
    }

    public boolean updatePassword(String email, String oldPassword, String newPassword) {
        return userRepository.findByEmail(email).map(user -> {
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) { // Hash password checking
                logger.warn("Update password failed: incorrect old password for user {}", email);
                return false;
            }

            user.setPassword(passwordEncoder.encode(newPassword)); // Hash new password
            userRepository.save(user);
            logger.info("Password updated successfully for user {}", email);
            return true;
        }).orElse(false);
    }

    public User registerCustomer(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hash password
        user.setName(request.getName());
        user.setRole(new Role(5, "Customer"));

        return userRepository.save(user);
    }
}
