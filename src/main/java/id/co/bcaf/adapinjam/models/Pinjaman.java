package id.co.bcaf.adapinjam.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pinjamans")
public class Pinjaman {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_user_customer", nullable = false)
    private UserCustomer customer;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "tenor", nullable = false)
    private Integer tenor;

    @Column(name = "angsuran", nullable = false)
    private Double angsuran;

    @Column(name = "bunga", nullable = false)
    private Double bunga;

    @Column(name = "sisa_tenor", nullable = false)
    private Integer sisaTenor;

    @Column(name = "sisa_ph", nullable = false)
    private Double sisaPokokHutang;

    @Column(name = "lunas", nullable = false)
    private Boolean lunas;

    @Column(name = "biaya_admin", nullable = false)
    private Double biayaAdmin;

    @Column(name = "total_dana_didapat", nullable = false)
    private Double totalDanaDidapat;
}

