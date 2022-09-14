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
    private String uId;

    public LoginDto(String accessToken, Long companyId, String uId) {
        this.setAccessToken(accessToken);
        this.companyId = companyId;
        this.uId = uId;
    }
}
