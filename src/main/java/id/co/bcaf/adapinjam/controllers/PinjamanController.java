package id.co.bcaf.adapinjam.controllers;

import id.co.bcaf.adapinjam.models.Pinjaman;
import id.co.bcaf.adapinjam.services.PinjamanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pinjaman")
public class PinjamanController {

    @Autowired
    private PinjamanService pinjamanService;

    @PostMapping("/{id}/lunas")
    public ResponseEntity<?> markLunas(@PathVariable UUID id) {
        pinjamanService.markAsLunas(id);
        return ResponseEntity.ok("Pinjaman lunas & cek upgrade plafon.");
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(pinjamanService.getByCustomerId(customerId));
    }
}

