package vn.clmart.manager_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserDTO {
    private String phone;
    private String birthDay;
    private String address;
    private String fullName;
    private String districtCode;
    private String provinceCode;
    private String wardsCode;
}
