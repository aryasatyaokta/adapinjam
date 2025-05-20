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
    private Double biayaAdmin;
    private Double totalDanaDidapat;
    private CustomerInfo customer;
    private List<ReviewNoteInfo> reviewNotes;

    public ReviewHistoryResponse(UUID pengajuanId, Double amount, int tenor, double bunga, Double angsuran,
                                 String status, Double biayaAdmin, Double totalDanaDidapat, CustomerInfo customer,
                                 List<ReviewNoteInfo> reviewNotes) {
        this.pengajuanId = pengajuanId;
        this.amount = amount;
        this.tenor = tenor;
        this.bunga = bunga;
        this.angsuran = angsuran;
        this.status = status;
        this.biayaAdmin = biayaAdmin;
        this.totalDanaDidapat = totalDanaDidapat;
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
        private String tempatLahir;
        private String tanggalLahir;
        private String jenisKelamin;
        private String noTelp;
        private String alamat;
        private String namaIbuKandung;
        private String fotoKtp;
        private String fotoSelfie;
        private String fotoProfil;
        private Double sisaPlafon;

        public CustomerInfo(String nama, String pekerjaan, String gaji, String noRek, String statusRumah,
                            String nik, String tempatLahir, String tanggalLahir, String jenisKelamin,String noTelp, String alamat,
                            String namaIbuKandung, String fotoKtp, String fotoSelfie, String fotoProfil, Double sisaPlafon) {
            this.nama = nama;
            this.pekerjaan = pekerjaan;
            this.gaji = gaji;
            this.noRek = noRek;
            this.statusRumah = statusRumah;
            this.nik = nik;
            this.tempatLahir = tempatLahir;
            this.tanggalLahir = tanggalLahir;
            this.jenisKelamin = jenisKelamin;
            this.noTelp = noTelp;
            this.alamat = alamat;
            this.namaIbuKandung = namaIbuKandung;
            this.fotoKtp = fotoKtp;
            this.fotoSelfie = fotoSelfie;
            this.fotoProfil = fotoProfil;
            this.sisaPlafon = sisaPlafon;
        }

        // Getters & Setters
    }
}
