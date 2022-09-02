package vn.clmart.manager_service.model;


import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.model.config.ListHashMapConverter;
import vn.clmart.manager_service.model.config.PersistableEntity;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Description("Table don hang")
public class Order extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String name;
    private String code;
    private Long idCustomer;
    @Convert(converter = ListHashMapConverter.class)
    @Column(columnDefinition = "text")
    private List<Map<Long, Integer>> detailItems;// bỏ
    private Long idEmployee;
}
