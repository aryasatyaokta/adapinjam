package id.co.bcaf.adapinjam.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String name_role;

    public Role(Integer id, String customer) {
        this.id = id;
        this.name_role = customer;
    }
}