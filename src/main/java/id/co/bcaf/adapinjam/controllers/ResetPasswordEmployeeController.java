package id.co.bcaf.adapinjam.controllers;

import id.co.bcaf.adapinjam.models.PasswordResetRequest;
import id.co.bcaf.adapinjam.services.ResetPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reset-password")
public class ResetPasswordEmployeeController {

    private final ResetPasswordService resetPasswordService;

//    @PreAuthorize("@accessPermission.hasAccess(authentication, 'REQ_RESET_PASSWORD')")
    @PostMapping("/employee")
    public ResponseEntity<String> forgotPasswordEmployee(@RequestBody Map<String, String> request) {
        String nip = request.get("nip");
        String response = resetPasswordService.requestResetPassword(nip);

        if (response.equals("Employee not found")) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }

//    @PreAuthorize("@accessPermission.hasAccess(authentication, 'GET_REQ_PASSWORD')")
    @GetMapping("/admin/requests")
    public ResponseEntity<List<PasswordResetRequest>> getResetRequests() {
        return ResponseEntity.ok(resetPasswordService.getPendingResetRequests());
    }

//    @PreAuthorize("@accessPermission.hasAccess(authentication, 'RES_RESET_PASSWORD')")
    @PostMapping("/admin/process/{id}")
    public ResponseEntity<String> manualResetPassword(@PathVariable UUID id) {
        String result = resetPasswordService.processPasswordReset(id);

        if (result.equals("Reset request not found")) {
            return ResponseEntity.notFound().build();
        }

        if (result.startsWith("Failed")) {
            return ResponseEntity.internalServerError().body(result);
        }

        return ResponseEntity.ok(result);
    }

//    @PreAuthorize("@accessPermission.hasAccess(authentication, 'INPUT_NEW_PASSWORD')")
    // Endpoint untuk employee memasukkan password baru dengan token
    @PostMapping("/employee/reset/{token}")
    public ResponseEntity<String> resetPasswordWithToken(@PathVariable String token, @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        String confirmPassword = request.get("confirmPassword");

        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        }

        String response = resetPasswordService.resetPasswordWithToken(token, newPassword);
        if (response.equals("Invalid or expired token")) {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }

        return ResponseEntity.ok("Password has been successfully reset");
    }
}
