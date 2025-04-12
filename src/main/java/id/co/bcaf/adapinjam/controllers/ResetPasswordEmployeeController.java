package id.co.bcaf.adapinjam.controllers;

import id.co.bcaf.adapinjam.models.PasswordResetRequest;
import id.co.bcaf.adapinjam.services.ResetPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reset-password")
public class ResetPasswordEmployeeController {

    private final ResetPasswordService resetPasswordService;

    // Employee mengajukan permintaan reset
    @PostMapping("/employee")
    public ResponseEntity<String> forgotPasswordEmployee(@RequestBody Map<String, String> request) {
        String nip = request.get("nip");
        String response = resetPasswordService.requestResetPassword(nip);

        if (response.equals("Employee not found")) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }

    // Super Admin lihat permintaan reset yang belum diproses
    @GetMapping("/admin/requests")
    public ResponseEntity<List<PasswordResetRequest>> getResetRequests() {
        return ResponseEntity.ok(resetPasswordService.getPendingResetRequests());
    }

    // Super Admin proses dan kirim password baru
    @PostMapping("/admin/process/{id}")
    public ResponseEntity<String> manualResetPassword(@PathVariable UUID id) {
        String result = resetPasswordService.manualResetPassword(id);

        if (result.equals("Reset request not found")) {
            return ResponseEntity.notFound().build();
        }

        if (result.startsWith("Failed")) {
            return ResponseEntity.internalServerError().body(result);
        }

        return ResponseEntity.ok(result);
    }
}
