package vn.soft.ship_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.soft.ship_service.dto.ShipperDTO;
import vn.soft.ship_service.model.config.LongArrayConverter;
import vn.soft.ship_service.model.config.PersistableEntity;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Shipper extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id", strategy = "vn.soft.ship_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String userId;
    @Convert(converter = LongArrayConverter.class)
    @Column(columnDefinition = "VARCHAR")
    private Long[] shopId;

    public static Shipper of(ShipperDTO shipperDTO, String userId) {
        Shipper shipper = Shipper.builder()
                .shopId(shipperDTO.getShopId())
                .userId(shipperDTO.getUserId())
                .build();
        shipper.setCreateBy(userId);
        shipper.setUpdateBy(userId);
        return shipper;
    }
}
