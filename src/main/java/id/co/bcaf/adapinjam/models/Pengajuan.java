package id.co.bcaf.adapinjam.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pengajuans")
public class Pengajuan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_user_customer", nullable = false)
    private UserCustomer customer;

    @Column(name = "angsuran", nullable = false)
    private Double angsuran;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "tenor", nullable = false)
    private Integer tenor;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "bunga", nullable = false)
    private Double bunga;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "marketing_approved_at")
    private LocalDateTime marketingApprovedAt;

    @Column(name = "branch_manager_approved_at")
    private LocalDateTime branchManagerApprovedAt;

    @Column(name = "back_office_approved_at")
    private LocalDateTime backOfficeApprovedAt;

    @Column(name = "disbursement_at")
    private LocalDateTime disbursementAt;
}
