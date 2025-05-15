package id.co.bcaf.adapinjam.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import id.co.bcaf.adapinjam.dtos.RegisterRequest;
import id.co.bcaf.adapinjam.models.FcmToken;
import id.co.bcaf.adapinjam.models.Role;
import id.co.bcaf.adapinjam.models.User;
import id.co.bcaf.adapinjam.models.UserEmployee;
import id.co.bcaf.adapinjam.repositories.FcmTokenRepository;
import id.co.bcaf.adapinjam.repositories.UserEmployeeRepository;
import id.co.bcaf.adapinjam.repositories.UserRepository;
import id.co.bcaf.adapinjam.utils.GoogleTokenVerifier;
import id.co.bcaf.adapinjam.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
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
    private final GoogleTokenVerifier googleTokenVerifier;
    private FcmTokenRepository fcmTokenRepository;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, UserEmployeeRepository userEmployeeRepository, JavaMailSender mailSender, GoogleTokenVerifier googleTokenVerifier, FcmTokenRepository fcmTokenRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userEmployeeRepository = userEmployeeRepository;
        this.mailSender = mailSender;
        this.googleTokenVerifier = googleTokenVerifier;
        this.fcmTokenRepository = fcmTokenRepository;
    }

    public String authenticateUser(String email, String password, String fcmToken) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (!user.isActive()) {
                logger.warn("User {} belum aktivasi email.", email);
                return null;
            }

            if (passwordEncoder.matches(password, user.getPassword())) {
                logger.info("User {} authenticated, generating token...", email);

                Optional<FcmToken> existing = fcmTokenRepository.findByUser_Email(email);
                if (existing.isPresent()) {
                    FcmToken tokenEntity = existing.get();
                    tokenEntity.setToken(fcmToken);
                    fcmTokenRepository.save(tokenEntity);
                } else {
                    FcmToken tokenEntity = new FcmToken();
                    tokenEntity.setToken(fcmToken);
                    tokenEntity.setUser(user);
                    fcmTokenRepository.save(tokenEntity);
                }

                return jwtUtil.generateToken(user);
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
                return jwtUtil.generateToken(userEmployee.getUser());
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

    public void registerCustomer(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setRole(new Role(5, "Customer"));
        user.setActive(false);

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateVerificationToken(user.getEmail());
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        String verificationLink = "http://localhost:4200/verify-email?token=" + encodedToken;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(user.getEmail());
            helper.setSubject("Verifikasi Email Anda");
            helper.setText("Silakan klik link berikut untuk verifikasi akun Anda: <br><a href=\""
                    + verificationLink + "\">Verifikasi Email</a>", true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengirim email verifikasi: " + e.getMessage());
        }
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

    public boolean verifyEmailToken(String token) {
        try {
            String email = jwtUtil.extractEmail(token);
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setActive(true);
                userRepository.save(user);
                return true;
            }
        } catch (ExpiredJwtException e) {
            logger.warn("Token expired: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Invalid token: {}", e.getMessage());
        }

        return false;
    }

    public String loginWithGoogle(String idTokenString) {
        GoogleIdToken.Payload payload = googleTokenVerifier.verify(idTokenString);
        if (payload == null) {
            logger.warn("Gagal memverifikasi ID Token Google.");
            return null;
        }

        String email = payload.getEmail();
        String name = (String) payload.get("name");

        Optional<User> userOpt = userRepository.findByEmail(email);
        User user;

        if (userOpt.isPresent()) {
            user = userOpt.get();
        } else {
            // Generate password random
            String randomPassword = UUID.randomUUID().toString().substring(0, 10);

            // Buat user baru
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setPassword(passwordEncoder.encode(randomPassword));
            user.setRole(new Role(5, "Customer")); // Sesuaikan dengan ID role Customer
            user.setActive(true);

            user = userRepository.save(user);

            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(email);
                helper.setSubject("Registrasi Berhasil via Google");
                helper.setText(
                        "Halo " + name + ",<br><br>" +
                                "Anda berhasil mendaftar melalui Google Sign-In.<br>" +
                                "Berikut adalah password Anda yang dapat digunakan untuk login manual:<br><br>" +
                                "<b>Password:</b> " + randomPassword + "<br><br>" +
                                "Silakan ubah password anda untuk keamanan data.<br><br>" +
                                "Salam,<br>Tim AdaPinjam", true
                );
                mailSender.send(message);
            } catch (Exception e) {
                logger.warn("Gagal mengirim email selamat datang: {}", e.getMessage());
            }
        }

        logger.info("User {} berhasil login via Google, generate token...", email);
        return jwtUtil.generateToken(user);
    }




}
