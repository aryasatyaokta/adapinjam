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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import java.util.Optional;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserEmployeeRepository userEmployeeRepository;
    private final JavaMailSender mailSender;
    private final Map<String, String> resetTokenMap = new ConcurrentHashMap<>();

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, UserEmployeeRepository userEmployeeRepository, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userEmployeeRepository = userEmployeeRepository;
        this.mailSender = mailSender;
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

    public void sendResetPasswordEmail(String email) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new Exception("User not found");
        }

        String token = jwtUtil.generateResetToken(email); // Custom method
        resetTokenMap.put(token, email);

        String resetLink = "http://localhost:3000/reset-password?token=" + token;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(email);
        helper.setSubject("Reset your password");
        helper.setText("Click the link to reset your password: " + resetLink, true);

        mailSender.send(message);
    }

    public boolean resetPassword(String token, String newPassword) {
        String email = resetTokenMap.get(token);
        if (email == null || !jwtUtil.validateToken(token, email)) {
            return false;
        }

        return userRepository.findByEmail(email).map(user -> {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            resetTokenMap.remove(token);
            return true;
        }).orElse(false);
    }


}
