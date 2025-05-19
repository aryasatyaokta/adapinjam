package id.co.bcaf.adapinjam.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class PengajuanHistoryResponse {
    private UUID pengajuanId;
    private Double amount;
    private Integer tenor;
    private Double bunga;
    private Double angsuran;
    private String status;
    private LocalDateTime marketingApprovedAt;
    private LocalDateTime branchManagerApprovedAt;
    private LocalDateTime backOfficeApprovedAt;
    private LocalDateTime disbursementAt;
    private Double biayaAdmin;
    private Double totalDanaDidapat;

    public PengajuanHistoryResponse(UUID pengajuanId, Double amount, Integer tenor, Double bunga, Double angsuran,
                                    String status, LocalDateTime marketingApprovedAt, LocalDateTime branchManagerApprovedAt,
                                    LocalDateTime backOfficeApprovedAt, LocalDateTime disbursementAt, Double biayaAdmin, Double totalDanaDidapat) {
        this.pengajuanId = pengajuanId;
        this.amount = amount;
        this.tenor = tenor;
        this.bunga = bunga;
        this.angsuran = angsuran;
        this.status = status;
        this.marketingApprovedAt = marketingApprovedAt;
        this.branchManagerApprovedAt = branchManagerApprovedAt;
        this.backOfficeApprovedAt = backOfficeApprovedAt;
        this.disbursementAt = disbursementAt;
        this.biayaAdmin = biayaAdmin;
        this.totalDanaDidapat = totalDanaDidapat;
    }
}
