package id.co.bcaf.adapinjam.models;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pengajuan_user_employees")
public class PengajuanToUserEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_pengajuan", nullable = false)
    private Pengajuan pengajuan;

    @ManyToOne
    @JoinColumn(name = "id_user_employee", nullable = false)
    private UserEmployee userEmployee;

    @Column(name = "catatan", columnDefinition = "TEXT")
    private String catatan;
}
