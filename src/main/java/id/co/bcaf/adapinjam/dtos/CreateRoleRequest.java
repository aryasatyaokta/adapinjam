package id.co.bcaf.adapinjam.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class CreateRoleRequest {
    private String name;
    private List<Long> featureIds;
}
