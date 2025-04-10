package id.co.bcaf.adapinjam.controllers;

import id.co.bcaf.adapinjam.dtos.*;
import id.co.bcaf.adapinjam.models.Pengajuan;
import id.co.bcaf.adapinjam.services.PengajuanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pengajuan")
public class PengajuanController {

    @Autowired
    private PengajuanService pengajuanService;

    @PostMapping("/create")
    public ResponseEntity<?> createPengajuan(@RequestBody PengajuanRequest request) {
        PengajuanResponse response = pengajuanService.createPengajuan(
                request.getCustomerId(),
                request.getAmount(),
                request.getTenor(),
                request.getBranchId()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/review")
    public ResponseEntity<?> review(@RequestBody ReviewRequest request) {
        pengajuanService.reviewPengajuan(request.getPengajuanId(), request.getEmployeeId(), request.isApproved(), request.getCatatan());
        return ResponseEntity.ok("Review success");
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getPengajuanByCustomer(@PathVariable UUID customerId) {
        List<Pengajuan> list = pengajuanService.getPengajuanByCustomerId(customerId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/to-review/{employeeId}")
    public ResponseEntity<?> getPengajuanToReview(@PathVariable UUID employeeId) {
        List<PengajuanWithNotesResponse> list = pengajuanService.getPengajuanToReviewByEmployee(employeeId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/review-history/{employeeId}")
    public ResponseEntity<?> getReviewHistory(@PathVariable UUID employeeId) {
        List<ReviewHistoryResponse> history = pengajuanService.getReviewHistoryByEmployee(employeeId);
        return ResponseEntity.ok(history);
    }

}
