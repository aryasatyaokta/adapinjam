package id.co.bcaf.adapinjam.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AuthResponse {
    private String token;
    public AuthResponse(String token) {
        this.token = token;
    }
}