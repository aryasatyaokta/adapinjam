package id.co.bcaf.adapinjam.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Data
public class MyReviewedPengajuanResponse {
    private UUID pengajuanId;
    private String customerName;
    private Double amount;
    private Integer tenor;
    private String status;
    private LocalDateTime createdAt;
    private Double biayaAmin;
    private Double totalDanaDidapat;

    public MyReviewedPengajuanResponse(UUID pengajuanId, String customerName, Double amount, Integer tenor, String status, LocalDateTime createdAt, Double biayaAmin, Double totalDanaDidapat) {
        this.pengajuanId = pengajuanId;
        this.customerName = customerName;
        this.amount = amount;
        this.tenor = tenor;
        this.status = status;
        this.createdAt = createdAt;
        this.biayaAmin = biayaAmin;
        this.totalDanaDidapat = totalDanaDidapat;
    }
    // getters & setters
}

