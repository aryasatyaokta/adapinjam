package id.co.bcaf.adapinjam.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AuthRequest {

    public AuthRequest() {}

    private String username;
    private String password;
    private String fcmToken;

    public AuthRequest(String username, String password, String fcmToken) {
        this.username = username;
        this.password = password;
        this.fcmToken = fcmToken;
    }
}