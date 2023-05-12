package vn.clmart.manager_service.model;


import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.OrderDTO;
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
@Description("Table don hang")
public class Order extends PersistableEntity<String> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.OrderGenerator")
    @GeneratedValue(generator = "id")
    private String id;
    private String name;
    private String code;
    private String state; // trạng thái đơn hàng
    private String phone;
    private String address;

    public static Order of(OrderDTO orderDto, String uid){
        Order order = Order.builder()
                .name(orderDto.getName())
                .code(orderDto.getCode())
                .address(orderDto.getAddress())
                .phone(orderDto.getPhone()).build();
        order.setCreateBy(uid);
        return order;
    }
}
