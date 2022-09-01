package vn.clmart.manager_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LoginDto {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long companyId;

    public LoginDto(String accessToken, Long companyId) {
        this.setAccessToken(accessToken);
        this.companyId = companyId;
    }
}
