package id.co.bcaf.adapinjam.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimulasiPengajuanResponse {
    private String jenisPlafon;
    private Double amount;
    private Integer tenor;
    private Double bunga;
    private Double angsuran;
    private Double totalPembayaran;
    private Double biayaAdmin;
    private Double danaCair;

    // constructor, getters, setters
}

