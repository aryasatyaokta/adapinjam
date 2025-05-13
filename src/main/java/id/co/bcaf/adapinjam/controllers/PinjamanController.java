package id.co.bcaf.adapinjam.controllers;

import id.co.bcaf.adapinjam.models.Pinjaman;
import id.co.bcaf.adapinjam.models.UserCustomer;
import id.co.bcaf.adapinjam.repositories.CustomerRepository;
import id.co.bcaf.adapinjam.services.PinjamanService;
import id.co.bcaf.adapinjam.utils.JwtUtil;
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
    private JwtUtil jwtUtil;

    @Autowired
    private PinjamanService pinjamanService;

    @Autowired
    private CustomerRepository customerRepo;

//    @PreAuthorize("@accessPermission.hasAccess(authentication, 'LUNAS_PEMBAYARAN')")
    @PostMapping("/{id}/lunas")
    public ResponseEntity<?> markLunas(@PathVariable UUID id) {
        pinjamanService.markAsLunas(id);
        return ResponseEntity.ok("Pinjaman lunas & cek upgrade plafon.");
    }

//    @PreAuthorize("@accessPermission.hasAccess(authentication, 'GET_PINJAMAN')")
    @GetMapping("/customer")
    public ResponseEntity<?> getByCustomerFromToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token); // Ambil email dari JWT

        UserCustomer customer = customerRepo.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return ResponseEntity.ok(pinjamanService.getByCustomerId(customer.getId()));
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

