package vn.clmart.manager_service.model;

import lombok.*;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.OrderDetailDTO;
import vn.clmart.manager_service.model.config.PersistableEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Description("Table các loại sản phẩm trong đơn hàng")
public class OrderDetail extends PersistableEntity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Long price; // giá mặt hàng tại thời điểm mua hàng
    private Integer number; // số lượng
    private Long totalPrice; // tổng tiền
    private Long productId; // id sản phẩm
    private String orderId; // id đơn hàng
    private Long typeProductId; // loại mặt hàng
    private String image;
    private String nameProduct;

    public static OrderDetail of(String uid, OrderDetailDTO orderDetailDTO){
        OrderDetail orderDetail = OrderDetail.builder()
                .price(orderDetailDTO.getPrice())
                .number(orderDetailDTO.getNumber())
                .totalPrice(orderDetailDTO.getTotalPrice())
                .productId(orderDetailDTO.getProductId())
                .typeProductId(orderDetailDTO.getTypeProductId())
                .orderId(orderDetailDTO.getOrderId()).build();
        orderDetail.setCreateBy(uid);
        orderDetail.setUpdateBy(uid);
        return orderDetail;
    }
}
