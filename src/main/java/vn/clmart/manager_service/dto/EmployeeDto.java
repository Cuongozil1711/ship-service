package vn.clmart.manager_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EmployeeDto {
    private UserLoginDto userLoginDto;
    private AddressDto addressDto;
    private FullNameDto fullNameDto;
    private String tel;
    private Long idPosition;
}
