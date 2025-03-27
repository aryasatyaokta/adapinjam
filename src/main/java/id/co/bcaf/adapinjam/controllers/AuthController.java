package id.co.bcaf.adapinjam.controllers;

import id.co.bcaf.adapinjam.dtos.AuthRequest;
import id.co.bcaf.adapinjam.dtos.AuthResponse;
import id.co.bcaf.adapinjam.dtos.RegisterRequest;
import id.co.bcaf.adapinjam.dtos.UpdatePassRequest;
import id.co.bcaf.adapinjam.services.AuthService;
import id.co.bcaf.adapinjam.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        String token = authService.authenticateUser(authRequest.getUsername(), authRequest.getPassword());
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

    @PostMapping("/register-customer")
    public ResponseEntity<AuthResponse> registerCustomer(@RequestBody RegisterRequest registerRequest) {
        try {
            authService.registerCustomer(registerRequest);
            String token = authService.authenticateUser(registerRequest.getUsername(), registerRequest.getPassword());

            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse("Invalid email or password"));
            }

            return ResponseEntity.ok(new AuthResponse(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse("Registration failed: " + e.getMessage()));
        }
    }


}
