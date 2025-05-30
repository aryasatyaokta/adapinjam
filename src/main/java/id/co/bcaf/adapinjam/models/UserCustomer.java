package id.co.bcaf.adapinjam.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_plafon", nullable = false)
    private Plafon plafon;

    @Column(name = "nik", nullable = false)
    private String nik;

    @Column(name = "tmpt_lahir", nullable = false)
    private String tempatLahir;

    @Column(name = "tgl_lahir", nullable = false)
    private String tanggalLahir;

    @Column(name = "jenis_kelamin", nullable = false)
    private String jenisKelamin;

    @Column(name = "no_telp", nullable = false)
    private String noTelp;

    @Column(name = "alamat", nullable = false)
    private String alamat;

    @Column(name = "nama_ibu_kandung", nullable = false)
    private String namaIbuKandung;

    @Column(name = "pekerjaan", nullable = false)
    private String pekerjaan;

    @Column(name = "gaji", nullable = false)
    private String gaji;

    @Column(name = "no_rek", nullable = false)
    private String noRek;

    @Column(name = "status_rumah", nullable = false)
    private String statusRumah;

    @Column(name = "sisa_plafon", nullable = false)
    private Double sisaPlafon;

    @Column(name = "foto_ktp")
    private String fotoKtp;

    @Column(name = "foto_selfie")
    private String fotoSelfie;

    @Column(name = "foto_profil")
    private String fotoProfil;
}
