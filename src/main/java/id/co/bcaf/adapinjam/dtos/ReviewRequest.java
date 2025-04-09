package id.co.bcaf.adapinjam.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReviewRequest {
    private UUID pengajuanId;
    private UUID employeeId;
    private boolean approved;
}
