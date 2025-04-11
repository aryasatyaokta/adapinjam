package id.co.bcaf.adapinjam.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReviewHistoryResponse {
    private UUID pengajuanId;
    private Double amount;
    private Integer tenor;
    private Double bunga;
    private Double angsuran;
    private String status;
    private String catatan;

    public ReviewHistoryResponse(UUID pengajuanId, Double amount, Integer tenor, Double bunga, Double angsuran, String status, String catatan) {
        this.pengajuanId = pengajuanId;
        this.amount = amount;
        this.tenor = tenor;
        this.bunga = bunga;
        this.angsuran = angsuran;
        this.status = status;
        this.catatan = catatan;
    }
}