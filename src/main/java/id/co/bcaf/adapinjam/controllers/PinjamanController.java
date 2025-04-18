package id.co.bcaf.adapinjam.controllers;

import id.co.bcaf.adapinjam.models.Pinjaman;
import id.co.bcaf.adapinjam.services.PinjamanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pinjaman")
public class PinjamanController {

    @Autowired
    private PinjamanService pinjamanService;

//    @PreAuthorize("@accessPermission.hasAccess(authentication, 'LUNAS_PEMBAYARAN')")
    @PostMapping("/{id}/lunas")
    public ResponseEntity<?> markLunas(@PathVariable UUID id) {
        pinjamanService.markAsLunas(id);
        return ResponseEntity.ok("Pinjaman lunas & cek upgrade plafon.");
    }

//    @PreAuthorize("@accessPermission.hasAccess(authentication, 'GET_PINJAMAN')")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(pinjamanService.getByCustomerId(customerId));
    }

//    @PreAuthorize("@accessPermission.hasAccess(authentication, 'PAID_PINJAMAN')")
    @PostMapping("/bayar/{id}")
    public ResponseEntity<?> bayarPinjaman(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> body
    ) {
        int jumlahTenor = (int) body.get("jumlahTenor");
        double jumlahBayar = Double.parseDouble(body.get("jumlahBayar").toString());

        pinjamanService.bayarPinjaman(id, jumlahTenor, jumlahBayar);
        return ResponseEntity.ok("Pembayaran berhasil diproses.");
    }


}

