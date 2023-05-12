package vn.clmart.manager_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OrderDTO {
    private String id;
    private String name;
    private String code;
    private Long idCustomer;
    private String state; // trạng thái đơn hàng
    private List<OrderDetailDTO> orderDetailDTOList;
    private String phone;
    private String address;
}
