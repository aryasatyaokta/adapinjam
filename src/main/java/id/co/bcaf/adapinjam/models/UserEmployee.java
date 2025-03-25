package id.co.bcaf.adapinjam.models;

import id.co.bcaf.adapinjam.enums.StatusEmployee;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user_employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String nip;

    @Column(name = "id_branch", nullable = false)
    private int branchId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_employee", nullable = false)
    private StatusEmployee statusEmployee;
}
