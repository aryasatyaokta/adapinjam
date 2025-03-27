package id.co.bcaf.adapinjam.models;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pengajuan_user_employees")
public class PengajuanToUserEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_pengajuan", nullable = false)
    private Pengajuan pengajuan;

    @ManyToOne
    @JoinColumn(name = "id_user_employee", nullable = false)
    private UserCustomer userCustomer;
}
