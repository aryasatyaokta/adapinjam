package id.co.bcaf.adapinjam.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePassRequest {
    private String oldPassword;
    private String newPassword;
}
