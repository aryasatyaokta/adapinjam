package id.co.bcaf.adapinjam.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PasswordResetRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    private UserEmployee userEmployee;

    private boolean processed = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    private String token;

    public PasswordResetRequest(String token, UserEmployee userEmployee) {
        this.token = token;
        this.userEmployee = userEmployee;
    }
}
