package id.co.bcaf.adapinjam.controllers;

import id.co.bcaf.adapinjam.dtos.*;
import id.co.bcaf.adapinjam.models.Pengajuan;
import id.co.bcaf.adapinjam.models.UserEmployee;
import id.co.bcaf.adapinjam.repositories.UserEmployeeRepository;
import id.co.bcaf.adapinjam.services.PengajuanService;
import id.co.bcaf.adapinjam.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pengajuan")
public class PengajuanController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PengajuanService pengajuanService;
    @Autowired
    private UserEmployeeRepository userEmployeeRepo;

//    @PreAuthorize("@accessPermission.hasAccess(authentication, 'CREATE_PENGAJUAN')")
    @PostMapping("/create")
    public ResponseEntity<?> createPengajuan(@RequestBody PengajuanRequest request) {
        PengajuanResponse response = pengajuanService.createPengajuan(
                request.getCustomerId(),
                request.getAmount(),
                request.getTenor(),
                request.getLatitude(),
                request.getLongitude()
        );
        return ResponseEntity.ok(response);
    }

//    @PreAuthorize("@accessPermission.hasAccess(authentication, 'REVIEW_PENGAJUAN')")
    @PostMapping("/review")
    public ResponseEntity<?> review(@RequestBody ReviewRequest request) {
        pengajuanService.reviewPengajuan(request.getPengajuanId(), request.getEmployeeId(), request.isApproved(), request.getCatatan());
        return ResponseEntity.ok("Review success");
    }

//    @PreAuthorize("@accessPermission.hasAccess(authentication, 'FEATURE_GET_IDPENGAJUAN_CUSTOMER')")
    @GetMapping("/history/{customerId}")
    public ResponseEntity<?> getHistoryPengajuanByCustomer(@PathVariable UUID customerId) {
        List<PengajuanHistoryResponse> historyList = pengajuanService.getPengajuanHistoryByCustomer(customerId);
        return ResponseEntity.ok(historyList);
    }

    @GetMapping("/to-review/{employeeId}")
    public ResponseEntity<?> getPengajuanToReview(@PathVariable UUID employeeId) {
        List<PengajuanWithNotesResponse> list = pengajuanService.getPengajuanToReviewByEmployee(employeeId);
        return ResponseEntity.ok(list);
    }

//    @PreAuthorize("@accessPermission.hasAccess(authentication, 'GET_REVIEW_HISTORY')")
//    @GetMapping("/review-history/{employeeId}")
//    public ResponseEntity<?> getReviewHistory(@PathVariable UUID employeeId) {
//        List<ReviewHistoryResponse> history = pengajuanService.getReviewHistoryByEmployee(employeeId);
//        return ResponseEntity.ok(history);
//    }

    @GetMapping("/review-history")
    public ResponseEntity<?> getReviewHistory() {
        // Ambil employeeId dari token JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Ambil username (email) dari token JWT

        // Ambil employeeId berdasarkan email
        Optional<UserEmployee> optionalEmployee = userEmployeeRepo.findByUserEmail(username);
        if (!optionalEmployee.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
        }

        UUID employeeId = optionalEmployee.get().getId();
        List<ReviewHistoryResponse> history = pengajuanService.getReviewHistoryByEmployee(employeeId);
        return ResponseEntity.ok(history);
    }



}
