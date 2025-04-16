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

    // Customer info as a nested object
    private CustomerInfo customer;

    public ReviewHistoryResponse(
            UUID pengajuanId, Double amount, Integer tenor, Double bunga,
            Double angsuran, String status, String catatan,
            CustomerInfo customer
    ) {
        this.pengajuanId = pengajuanId;
        this.amount = amount;
        this.tenor = tenor;
        this.bunga = bunga;
        this.angsuran = angsuran;
        this.status = status;
        this.catatan = catatan;
        this.customer = customer;
    }

    // Nested CustomerInfo class
    @Getter
    @Setter
    public static class CustomerInfo {
        private String namaCustomer;
        private String pekerjaan;
        private String gaji;
        private String noRek;
        private String statusRumah;
        private String nik;
        private String tempatTglLahir;
        private String noTelp;
        private String alamat;
        private String namaIbuKandung;
        private Double sisaPlafon;

        public CustomerInfo(String namaCustomer, String pekerjaan, String gaji, String noRek,
                            String statusRumah, String nik, String tempatTglLahir, String noTelp,
                            String alamat, String namaIbuKandung, Double sisaPlafon) {
            this.namaCustomer = namaCustomer;
            this.pekerjaan = pekerjaan;
            this.gaji = gaji;
            this.noRek = noRek;
            this.statusRumah = statusRumah;
            this.nik = nik;
            this.tempatTglLahir = tempatTglLahir;
            this.noTelp = noTelp;
            this.alamat = alamat;
            this.namaIbuKandung = namaIbuKandung;
            this.sisaPlafon = sisaPlafon;
        }
    }
}

