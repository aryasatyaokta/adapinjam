package id.co.bcaf.adapinjam.dtos;

import lombok.Data;
import java.util.UUID;

@Data
public class RegisterRequest {
    private String name;
    private String username;
    private String password;
}


