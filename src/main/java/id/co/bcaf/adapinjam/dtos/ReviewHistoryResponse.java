package id.co.bcaf.adapinjam.dtos;
import id.co.bcaf.adapinjam.dtos.ReviewNoteInfo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ReviewHistoryResponse {
    private UUID pengajuanId;
    private Double amount;
    private int tenor;
    private double bunga;
    private Double angsuran;
    private String status;
    private CustomerInfo customer;
    private List<ReviewNoteInfo> reviewNotes;

    public ReviewHistoryResponse(UUID pengajuanId, Double amount, int tenor, double bunga, Double angsuran,
                                 String status, CustomerInfo customer,
                                 List<ReviewNoteInfo> reviewNotes) {
        this.pengajuanId = pengajuanId;
        this.amount = amount;
        this.tenor = tenor;
        this.bunga = bunga;
        this.angsuran = angsuran;
        this.status = status;
        this.customer = customer;
        this.reviewNotes = reviewNotes;
    }

    // Getters & Setters
    @Getter
    @Setter
    public static class CustomerInfo {
        private String nama;
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

        public CustomerInfo(String nama, String pekerjaan, String gaji, String noRek, String statusRumah,
                            String nik, String tempatTglLahir, String noTelp, String alamat,
                            String namaIbuKandung, Double sisaPlafon) {
            this.nama = nama;
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

        // Getters & Setters
    }
}
