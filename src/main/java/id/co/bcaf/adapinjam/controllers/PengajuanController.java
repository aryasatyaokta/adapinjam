package id.co.bcaf.adapinjam.controllers;

import id.co.bcaf.adapinjam.dtos.*;
import id.co.bcaf.adapinjam.models.Pengajuan;
import id.co.bcaf.adapinjam.models.PengajuanToUserEmployee;
import id.co.bcaf.adapinjam.models.UserCustomer;
import id.co.bcaf.adapinjam.models.UserEmployee;
import id.co.bcaf.adapinjam.repositories.CustomerRepository;
import id.co.bcaf.adapinjam.repositories.PengajuanToUserEmployeeRepository;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/pengajuan")
public class PengajuanController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PengajuanService pengajuanService;
    @Autowired
    private UserEmployeeRepository userEmployeeRepo;
    @Autowired
    private PengajuanToUserEmployeeRepository pengajuanUserRepo;
    @Autowired
    private CustomerRepository customerRepo;


//    @PreAuthorize("@accessPermission.hasAccess(authentication, 'CREATE_PENGAJUAN')")
    @PostMapping("/create")
    public ResponseEntity<?> createPengajuan(
            @RequestBody PengajuanRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token); // Ganti dengan metode yang sesuai untuk ambil email dari token

        UserCustomer customer = customerRepo.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        PengajuanResponse response = pengajuanService.createPengajuan(
                customer,
                request.getAmount(),
                request.getTenor(),
                request.getLatitude(),
                request.getLongitude()
        );

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'REVIEW_PENGAJUAN')")
    @PostMapping("/review")
    public ResponseEntity<?> review(@RequestBody ReviewRequest request, Authentication authentication) {
        // Ambil employeeId dari token
        String username = authentication.getName(); // Asumsikan username = email
        UserEmployee employee = userEmployeeRepo.findByUserEmail(username)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Ambil role dan cari pengajuan yang statusnya sesuai bucket
        int roleId = employee.getUser().getRole().getId();

        // Tentukan status bucket saat ini berdasarkan role
        String currentBucket = switch (roleId) {
            case 2 -> "BCKT_MARKETING";
            case 3 -> "BCKT_BRANCHMANAGER";
            case 4 -> "BCKT_BACKOFFICE";
            default -> throw new RuntimeException("Invalid role");
        };

        // Cari pengajuan yang masuk bucket tersebut dan ditugaskan ke employee ini
        List<PengajuanToUserEmployee> links;
        links = pengajuanUserRepo.findByUserEmployee_Id(employee.getId());
        Optional<PengajuanToUserEmployee> currentLink = links.stream()
                .filter(link -> link.getPengajuan().getStatus().equals(currentBucket))
                .findFirst();

        if (currentLink.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tidak ada pengajuan yang sedang masuk ke bucket Anda");
        }

        UUID pengajuanId = currentLink.get().getPengajuan().getId();

        // Panggil service seperti biasa
        pengajuanService.reviewPengajuan(pengajuanId, employee.getId(), request.isApproved(), request.getCatatan());

        return ResponseEntity.ok("Review success");
    }

    //    @PreAuthorize("@accessPermission.hasAccess(authentication, 'FEATURE_GET_IDPENGAJUAN_CUSTOMER')")
    @GetMapping("/history-customer")
    public ResponseEntity<?> getHistoryPengajuanByCustomer(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token); // Ambil email dari JWT

        UserCustomer customer = customerRepo.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<PengajuanHistoryResponse> historyList = pengajuanService.getPengajuanHistoryByCustomer(customer.getId());
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

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'REVIEW_HISTORY')")
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

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'REVIEW_HISTORY_BY_ID')")
    @GetMapping("/my-reviewed-pengajuan")
    public ResponseEntity<?> getMyReviewedPengajuan() {
        // Ambil employee dari JWT token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<UserEmployee> optionalEmployee = userEmployeeRepo.findByUserEmail(username);
        if (optionalEmployee.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
        }

        UUID employeeId = optionalEmployee.get().getId();

        // Ambil daftar pengajuan yang pernah direview employee ini
        List<PengajuanToUserEmployee> reviewedList = pengajuanUserRepo.findByUserEmployeeId(employeeId);

        // Ubah ke response DTO
        List<MyReviewedPengajuanResponse> responseList = reviewedList.stream()
                .map(link -> {
                    Pengajuan pengajuan = link.getPengajuan();
                    return new MyReviewedPengajuanResponse(
                            pengajuan.getId(),
                            pengajuan.getCustomer().getUser().getName(),
                            pengajuan.getAmount(),
                            pengajuan.getTenor(),
                            pengajuan.getStatus(),
                            pengajuan.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }




}
