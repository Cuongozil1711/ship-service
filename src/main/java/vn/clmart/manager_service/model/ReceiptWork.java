package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.model.config.PersistableEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Description("Table phieu giao ca lam")
public class ReceiptWork extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String name;
    private String code;
    private Date dateWork; // thời gian làm việc
    private String shift; // ca làm việc
}
