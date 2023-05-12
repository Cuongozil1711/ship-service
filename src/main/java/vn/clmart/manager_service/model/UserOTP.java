package vn.clmart.manager_service.model;

import lombok.*;
import vn.clmart.manager_service.model.config.PersistableEntity;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserOTP extends PersistableEntity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String otp;
    private String userId;
}
