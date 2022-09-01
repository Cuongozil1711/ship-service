package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.model.config.PersistableEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Description("Table mat hang")
public class Items extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code;
    private String name;
    private Long idCategory;
    private Long idItems;
    private Long idPubliser;
    private Long idStall;
}
