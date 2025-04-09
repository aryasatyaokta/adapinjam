package id.co.bcaf.adapinjam.services;

import id.co.bcaf.adapinjam.dtos.PengajuanResponse;
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

import java.util.List;
import java.util.UUID;

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

        double bunga = 5.0;
        double angsuran = calculateAngsuran(amount, tenor, bunga);

        Pengajuan pengajuan = new Pengajuan();
        pengajuan.setCustomer(customer);
        pengajuan.setAmount(amount);
        pengajuan.setTenor(tenor);
        pengajuan.setBunga(bunga);
        pengajuan.setAngsuran(angsuran);
        pengajuan.setStatus("BCKT_MARKETING");
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
                marketing.getUser().getName()
        );
    }

    public void reviewPengajuan(UUID pengajuanId, UUID employeeId, boolean isApproved) {
        PengajuanToUserEmployee link = pengajuanUserRepo.findByPengajuanIdAndUserEmployeeId(pengajuanId, employeeId)
                .orElseThrow(() -> new RuntimeException("Pengajuan not found or not assigned to this employee"));

        Pengajuan pengajuan = link.getPengajuan();
        UserEmployee employee = link.getUserEmployee();

        // Role validation per bucket
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
            pengajuan.setStatus("REJECT_" + employee.getUser().getRole().getName_role().toUpperCase());
        } else {
            switch (status) {
                case "BCKT_MARKETING" -> pengajuan.setStatus("BCKT_BRANCHMANAGER");
                case "BCKT_BRANCHMANAGER" -> pengajuan.setStatus("BCKT_BACKOFFICE");
                case "BCKT_BACKOFFICE" -> {
                    pengajuan.setStatus("DISBURSEMENT");
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
        return total / tenor;
    }

    public List<Pengajuan> getPengajuanToReviewByEmployee(UUID employeeId) {
        List<PengajuanToUserEmployee> links = pengajuanUserRepo.findByUserEmployeeId(employeeId);
        return links.stream().map(PengajuanToUserEmployee::getPengajuan).toList();
    }

}
