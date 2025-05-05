package id.co.bcaf.adapinjam.dtos;

import lombok.Data;

@Data
public class CustomerRequest {
    private Integer idPlafon;
    private String nik;
    private String tempatTglLahir;
    private String noTelp;
    private String alamat;
    private String namaIbuKandung;
    private String pekerjaan;
    private String gaji;
    private String noRek;
    private String statusRumah;
    private String jenisKelamin;
//    private Double sisaPlafon;
}
