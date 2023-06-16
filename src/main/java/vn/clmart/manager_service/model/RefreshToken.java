package vn.clmart.manager_service.model;

import lombok.*;


import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Instant expiryDate;
}
