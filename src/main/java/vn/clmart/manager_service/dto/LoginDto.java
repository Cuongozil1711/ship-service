package vn.clmart.manager_service.dto;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LoginDto {
    private String accessToken;
    private String tokenType = "Bearer";
    private String uId;
    private String role;
    private String fullName;
    private String name;
    private String image;
    private Date birthDay;
    private String jwt;
    private String refreshToken;

    public LoginDto(String accessToken, String uId, String role, String fullName) {
        this.setAccessToken(accessToken);
        this.uId = uId;
        this.role = role;
        this.fullName = fullName;
    }
}
