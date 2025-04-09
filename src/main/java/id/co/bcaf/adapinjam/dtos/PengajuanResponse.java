package id.co.bcaf.adapinjam.dtos;

import id.co.bcaf.adapinjam.models.UserCustomer;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PengajuanResponse {
    private UUID pengajuanId;
    private UserCustomer customer;
    private Double amount;
    private Integer tenor;
    private Double bunga;
    private Double angsuran;
    private String status;
    private UUID branchId;
    private String namaMarketing;
}
