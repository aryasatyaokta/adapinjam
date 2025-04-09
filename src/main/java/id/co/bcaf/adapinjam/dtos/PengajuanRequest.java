package id.co.bcaf.adapinjam.dtos;

import id.co.bcaf.adapinjam.models.UserCustomer;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PengajuanRequest {
    private UUID customerId;
    private Double amount;
    private Integer tenor;
    private UUID branchId;
}
