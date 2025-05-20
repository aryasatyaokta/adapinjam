package id.co.bcaf.adapinjam.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PengajuanPreviewResponse {
    private Double amount;
    private Integer tenor;
    private Double bunga;
    private Double angsuran;
    private Double biayaAdmin;
    private Double totalDanaDidapat;
}


