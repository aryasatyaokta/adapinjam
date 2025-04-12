package id.co.bcaf.adapinjam.services;

import id.co.bcaf.adapinjam.dtos.PengajuanHistoryResponse;
import id.co.bcaf.adapinjam.dtos.PengajuanResponse;
import id.co.bcaf.adapinjam.dtos.PengajuanWithNotesResponse;
import id.co.bcaf.adapinjam.dtos.ReviewHistoryResponse;
import id.co.bcaf.adapinjam.models.Pengajuan;
import id.co.bcaf.adapinjam.models.PengajuanToUserEmployee;
import id.co.bcaf.adapinjam.models.UserCustomer;
import id.co.bcaf.adapinjam.models.UserEmployee;
import id.co.bcaf.adapinjam.repositories.CustomerRepository;
import id.co.bcaf.adapinjam.repositories.PengajuanRepository;
import id.co.bcaf.adapinjam.repositories.PengajuanToUserEmployeeRepository;
import id.co.bcaf.adapinjam.repositories.UserEmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PengajuanService {
    @Autowired
    private PengajuanRepository pengajuanRepo;

    @Autowired
    private PengajuanToUserEmployeeRepository pengajuanUserRepo;

    @Autowired
    private UserEmployeeRepository userEmployeeRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private PinjamanService pinjamanService;

    public List<Pengajuan> getPengajuanByCustomerId(UUID customerId) {
        return pengajuanRepo.findByCustomer_Id(customerId);
    }

    @Transactional
    public PengajuanResponse createPengajuan(UUID customerId, Double amount, Integer tenor, UUID branchId) {

        UserCustomer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (customer.getSisaPlafon() < amount) {
            throw new RuntimeException("Sisa plafon tidak mencukupi untuk pengajuan ini");
        }

        double bunga = customer.getPlafon().getBunga();
        double angsuran = calculateAngsuran(amount, tenor, bunga);

        Pengajuan pengajuan = new Pengajuan();
        pengajuan.setCustomer(customer);
        pengajuan.setAmount(amount);
        pengajuan.setTenor(tenor);
        pengajuan.setBunga(bunga);
        pengajuan.setAngsuran(angsuran);
        pengajuan.setStatus("BCKT_MARKETING");
        pengajuan.setCreatedAt(LocalDateTime.now());
        pengajuanRepo.save(pengajuan);

        customer.setSisaPlafon(customer.getSisaPlafon() - amount);
        customerRepo.save(customer);

        UserEmployee marketing = userEmployeeRepo.findRandomMarketingByBranch(branchId);
        if (marketing == null) {
            throw new RuntimeException("No marketing available for this branch");
        }

        PengajuanToUserEmployee link = new PengajuanToUserEmployee();
        link.setPengajuan(pengajuan);
        link.setUserEmployee(marketing);
        pengajuanUserRepo.save(link);

        return new PengajuanResponse(
                pengajuan.getId(),
                customer,
                pengajuan.getAmount(),
                pengajuan.getTenor(),
                pengajuan.getBunga(),
                pengajuan.getAngsuran(),
                pengajuan.getStatus(),
                marketing.getBranch().getId(),
                marketing.getUser().getName(),
                pengajuan.getCreatedAt(),
                pengajuan.getMarketingApprovedAt(),
                pengajuan.getBranchManagerApprovedAt(),
                pengajuan.getBackOfficeApprovedAt()
        );
    }

    public void reviewPengajuan(UUID pengajuanId, UUID employeeId, boolean isApproved, String catatan) {
        PengajuanToUserEmployee link = pengajuanUserRepo.findByPengajuanIdAndUserEmployeeId(pengajuanId, employeeId)
                .orElseThrow(() -> new RuntimeException("Pengajuan not found or not assigned to this employee"));

        Pengajuan pengajuan = link.getPengajuan();
        UserEmployee employee = link.getUserEmployee();

        link.setCatatan(catatan);
        pengajuanUserRepo.save(link);

        String status = pengajuan.getStatus();
        int employeeRole = employee.getUser().getRole().getId();

        boolean isRoleValid =
                (status.equals("BCKT_MARKETING") && employeeRole == 4) ||
                        (status.equals("BCKT_BRANCHMANAGER") && employeeRole == 3) ||
                        (status.equals("BCKT_BACKOFFICE") && employeeRole == 2);

        if (!isRoleValid) {
            throw new RuntimeException("You do not have permission to review this pengajuan in this status");
        }

        if (!isApproved) {
            // Restore sisa plafon
            UserCustomer customer = pengajuan.getCustomer();
            customer.setSisaPlafon(customer.getSisaPlafon() + pengajuan.getAmount());
            customerRepo.save(customer);

            pengajuan.setStatus("REJECT_" + employee.getUser().getRole().getName_role().toUpperCase());
            pengajuanRepo.save(pengajuan);
            return;
        }

        switch (status) {
            case "BCKT_MARKETING" -> {
                pengajuan.setStatus("BCKT_BRANCHMANAGER");
                pengajuan.setMarketingApprovedAt(LocalDateTime.now());  // Set tanggal approval Marketing
            }
            case "BCKT_BRANCHMANAGER" -> {
                pengajuan.setStatus("BCKT_BACKOFFICE");
                pengajuan.setBranchManagerApprovedAt(LocalDateTime.now());  // Set tanggal approval Branch Manager
            }
            case "BCKT_BACKOFFICE" -> {
                pengajuan.setStatus("DISBURSEMENT");
                pengajuan.setBackOfficeApprovedAt(LocalDateTime.now());  // Set tanggal approval Back Office
                pengajuanRepo.save(pengajuan);

                // Simpan sebagai history pinjaman
                pinjamanService.createPinjamanFromPengajuan(
                        pengajuan.getCustomer(),
                        pengajuan.getAmount(),
                        pengajuan.getTenor(),
                        pengajuan.getBunga(),
                        pengajuan.getAngsuran()
                );
                return;
            }
        }

        pengajuanRepo.save(pengajuan);

        // Assign ke reviewer berikutnya jika disetujui
        if (isApproved) {
            int nextRoleId = switch (pengajuan.getStatus()) {
                case "BCKT_BRANCHMANAGER" -> 3;
                case "BCKT_BACKOFFICE" -> 2;
                default -> 0;
            };
            if (nextRoleId > 0) assignToNextReviewer(pengajuan, nextRoleId);
        }
    }



    private void assignToNextReviewer(Pengajuan pengajuan, int roleId) {
        List<PengajuanToUserEmployee> links = pengajuanUserRepo.findByPengajuanId(pengajuan.getId());
        if (links.isEmpty()) throw new RuntimeException("No UserEmployee assigned to pengajuan");

        UserEmployee lastReviewer = links.get(links.size() - 1).getUserEmployee();
        UUID branchId = lastReviewer.getBranch().getId();

        List<UserEmployee> candidates = userEmployeeRepo.findByBranchIdAndUserRoleId(branchId, roleId);
        if (candidates.isEmpty()) throw new RuntimeException("No reviewer available for next role");

        UserEmployee selected = candidates.get(0); // implementasi sederhana, bisa dibuat lebih optimal

        PengajuanToUserEmployee newLink = new PengajuanToUserEmployee();
        newLink.setPengajuan(pengajuan);
        newLink.setUserEmployee(selected);
        pengajuanUserRepo.save(newLink);
    }

    private double calculateAngsuran(double amount, int tenor, double bunga) {
        double total = amount + (amount * bunga / 100);
        double rawAngsuran = total / tenor;
        return Math.round(rawAngsuran * 100.0) / 100.0;
    }

    public List<PengajuanWithNotesResponse> getPengajuanToReviewByEmployee(UUID employeeId) {
        List<PengajuanToUserEmployee> links = pengajuanUserRepo.findByUserEmployeeId(employeeId);

        return links.stream().map(link -> {
            Pengajuan pengajuan = link.getPengajuan();
            UserCustomer customer = pengajuan.getCustomer();  // Ambil data customer
            List<String> catatanList = pengajuanUserRepo.findByPengajuanId(pengajuan.getId()).stream()
                    .map(PengajuanToUserEmployee::getCatatan)
                    .filter(catatan -> catatan != null)  // Menghindari null
                    .collect(Collectors.toList());

            return new PengajuanWithNotesResponse(pengajuan, customer, catatanList);
        }).collect(Collectors.toList());
    }

    public List<ReviewHistoryResponse> getReviewHistoryByEmployee(UUID employeeId) {
        // Ambil semua pengajuan yang pernah ditinjau oleh employeeId
        List<PengajuanToUserEmployee> links = pengajuanUserRepo.findByUserEmployeeId(employeeId);

        // Ambil role ID dari employee yang sedang mengakses
        UserEmployee employee = userEmployeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        int employeeRole = employee.getUser().getRole().getId(); // Role ID: 2 (Back Office), 3 (Branch Manager), 4 (Marketing)

        // Memfilter pengajuan berdasarkan status yang relevan untuk role ini
        return links.stream().map(link -> {
                    Pengajuan pengajuan = link.getPengajuan();
                    String status = pengajuan.getStatus();

                    // Pastikan pengajuan hanya dikembalikan jika statusnya sesuai dengan peran employee
                    if (isStatusValidForRole(status, employeeRole)) {
                        String catatan = link.getCatatan();

                        return new ReviewHistoryResponse(
                                pengajuan.getId(),
                                pengajuan.getAmount(),
                                pengajuan.getTenor(),
                                pengajuan.getBunga(),
                                pengajuan.getAngsuran(),
                                pengajuan.getStatus(),
                                catatan
                        );
                    }
                    return null;  // Jika status tidak valid untuk role, kembalikan null
                })
                .filter(Objects::nonNull) // Filter null values
                .collect(Collectors.toList());
    }

    private boolean isStatusValidForRole(String status, int roleId) {
        // Pastikan status sesuai dengan peran masing-masing
        switch (roleId) {
            case 4: // Marketing
                return status.equals("BCKT_MARKETING");
            case 3: // Branch Manager
                return status.equals("BCKT_BRANCHMANAGER");
            case 2: // Back Office
                return status.equals("BCKT_BACKOFFICE");
            default:
                return false;
        }
    }

    public List<PengajuanHistoryResponse> getPengajuanHistoryByCustomer(UUID customerId) {
        List<Pengajuan> pengajuanList = pengajuanRepo.findByCustomer_Id(customerId);

        return pengajuanList.stream().map(pengajuan -> {
            // Ambil tanggal approve untuk setiap role

            return new PengajuanHistoryResponse(
                    pengajuan.getId(),
                    pengajuan.getAmount(),
                    pengajuan.getTenor(),
                    pengajuan.getBunga(),
                    pengajuan.getAngsuran(),
                    pengajuan.getStatus(),
                    pengajuan.getMarketingApprovedAt(),
                    pengajuan.getBranchManagerApprovedAt(),
                    pengajuan.getBackOfficeApprovedAt(),
                    pengajuan.getDisbursementAt()
            );
        }).collect(Collectors.toList());
    }

    private Date getApprovedDate(Pengajuan pengajuan, String status) {
        // Cari catatan pengajuan untuk mendapatkan tanggal approval
        List<PengajuanToUserEmployee> links = pengajuanUserRepo.findByPengajuanId(pengajuan.getId());
        for (PengajuanToUserEmployee link : links) {
            if (pengajuan.getStatus().equals(status)) {
                // Ambil tanggal approve dari pengajuan jika ada
                return new Date(); // Implementasikan logika untuk mendapatkan tanggal sesuai catatan atau status
            }
        }
        return null; // Jika tidak ada, return null
    }


}
