package id.co.bcaf.adapinjam.dtos;

import id.co.bcaf.adapinjam.models.Pengajuan;
import id.co.bcaf.adapinjam.models.UserCustomer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter

public class PengajuanWithNotesResponse {
    private UUID pengajuanId;
    private Double amount;
    private Integer tenor;
    private Double bunga;
    private Double angsuran;
    private String status;
    private UserCustomer customer;
    private List<String> catatanList;  // Daftar catatan

    // Constructor, Getter, and Setter

    public PengajuanWithNotesResponse(Pengajuan pengajuan,UserCustomer customer, List<String> catatanList) {
        this.customer = customer;
        this.pengajuanId = pengajuan.getId();
        this.amount = pengajuan.getAmount();
        this.tenor = pengajuan.getTenor();
        this.bunga = pengajuan.getBunga();
        this.angsuran = pengajuan.getAngsuran();
        this.status = pengajuan.getStatus();
        this.catatanList = catatanList;
    }

    // Getter and Setter methods...
}
