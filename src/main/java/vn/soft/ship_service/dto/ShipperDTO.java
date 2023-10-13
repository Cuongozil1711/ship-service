package vn.soft.ship_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vn.soft.ship_service.config.base.BaseDTO;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShipperDTO extends BaseDTO {
    private Long id;
    private String userId;
    private Long[] shopId;
}
