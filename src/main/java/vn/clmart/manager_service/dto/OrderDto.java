package vn.clmart.manager_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.clmart.manager_service.model.config.ListHashMapConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OrderDto {
    private String name;
    private String code;
    private Long idCustomer;
    private List<Map<String, Integer>> detailItems;// bỏ
}
