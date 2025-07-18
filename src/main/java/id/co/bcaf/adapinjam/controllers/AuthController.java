package id.co.bcaf.adapinjam.controllers;

import id.co.bcaf.adapinjam.dtos.*;
import id.co.bcaf.adapinjam.models.User;
import id.co.bcaf.adapinjam.models.UserEmployee;
import id.co.bcaf.adapinjam.repositories.UserEmployeeRepository;
import id.co.bcaf.adapinjam.repositories.UserRepository;
import id.co.bcaf.adapinjam.services.AuthService;
import id.co.bcaf.adapinjam.services.TokenBlacklistService;
import id.co.bcaf.adapinjam.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthService authService;
    @Autowired
    private TokenBlacklistService tokenBlacklistService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserEmployeeRepository userEmployeeRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login-google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody GoogleAuthRequest request) {
        String token = authService.loginWithGoogle(request.getIdToken());

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "ID token Google tidak valid atau gagal verifikasi."));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Login dengan Google berhasil.",
                "token", token
        ));
    }

//    @PreAuthorize("@accessPermission.hasAccess(authentication, 'LOGIN_CUSTOMER')")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        String token = authService.authenticateUser(
                authRequest.getUsername(),
                authRequest.getPassword(),
                authRequest.getFcmToken()
        );

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        return ResponseEntity.ok(new AuthResponse(token));
    }


    @GetMapping("/test")
    public ResponseEntity<String> testToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            try {
                return ResponseEntity.ok("Email: " + jwtUtil.extractEmail(token));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token: " + e.getMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid Authorization header");
    }

//    @PreAuthorize("@accessPermission.hasAccess(authentication, 'UPDATE_PASSWORD')")
    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdatePassRequest request) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7).trim();
        String email = jwtUtil.extractEmail(token);

        if (authService.updatePassword(email, request.getOldPassword(), request.getNewPassword())) {
            return ResponseEntity.ok("Password updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid old password or user not found");
        }
    }

//    @PreAuthorize("@accessPermission.hasAccess(authentication, 'REGISTER_CUSTOMER')")
    @PostMapping("/register-customer")
    public ResponseEntity<AuthResponse> registerCustomer(@RequestBody RegisterRequest registerRequest) {
        try {
            authService.registerCustomer(registerRequest);
            return ResponseEntity.ok(new AuthResponse("Registrasi berhasil! Silakan cek email untuk verifikasi."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse("Registration failed: " + e.getMessage()));
        }
    }

    //    @PreAuthorize("@accessPermission.hasAccess(authentication, 'LOGOUT')")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            // Blacklist the token
            tokenBlacklistService.blacklistToken(token);
            return ResponseEntity.ok("Logged out successfully");
        }
        return ResponseEntity.badRequest().body("Invalid or missing Authorization header");
    }

//    @PreAuthorize("@accessPermission.hasAccess(authentication, 'LOGIN_EMPLOYEE')")
@PostMapping("/login-employee")
public ResponseEntity<?> loginEmployee(@RequestBody AuthRequest authRequest) {
    Optional<UserEmployee> optionalUserEmployee = userEmployeeRepository.findByNip(authRequest.getUsername());

    if (optionalUserEmployee.isPresent()) {
        UserEmployee userEmployee = optionalUserEmployee.get();

        if (passwordEncoder.matches(authRequest.getPassword(), userEmployee.getUser().getPassword())) {

            String token = jwtUtil.generateToken(userEmployee.getUser());

            // Jika user belum aktif (harus update password)
            if (!userEmployee.getUser().isActive()) {
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            }

            // Jika user aktif → login biasa
            return ResponseEntity.ok(new AuthResponse(token));
        }
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid NIP or password");
}


    //    @PreAuthorize("@accessPermission.hasAccess(authentication, 'FORGOT_PASSWORD_CUSTOMER')")
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        try {
            authService.sendResetPasswordEmail(email);
            return ResponseEntity.ok("Reset password email sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email: " + e.getMessage());
        }
    }



//    @PreAuthorize("@accessPermission.hasAccess(authentication, 'RESET_PASSWORD_CUSTOMER')")
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        boolean success = authService.resetPassword(token, newPassword);
        if (success) {
            return ResponseEntity.ok("Password reset successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        boolean verified = authService.verifyEmailToken(token);
        if (verified) {
            return ResponseEntity.ok("Email berhasil diverifikasi. Silakan login.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token tidak valid atau kadaluarsa.");
        }
    }

}
