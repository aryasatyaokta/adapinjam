package id.co.bcaf.adapinjam.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Data
public class RegisterUserEmployeeRequest {

    private UUID user; // ID user yang didapat dari tabel users
    private UUID branch; // ID branch yang didapat dari tabel branches
    private String nip;  // NIP dari UserEmployee
    private String statusEmployee; // Status employee (misalnya ACTIVE, RESIGNED, dll)
}
