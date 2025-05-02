package id.co.bcaf.adapinjam.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    private String message;

    @Column(name = "is_read")  // Mengganti nama kolom jadi is_read
    private boolean read = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}
