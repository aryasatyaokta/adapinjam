package id.co.bcaf.adapinjam.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "plafons")
public class Plafon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idPlafon;

    @Column(name = "jenis_plafon", length = 20, nullable = false)
    private String jenisPlafon;

    @Column(name = "jumlah_plafon", nullable = false)
    private Double jumlahPlafon;
}
