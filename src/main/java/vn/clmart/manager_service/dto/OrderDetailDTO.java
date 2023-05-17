package vn.clmart.manager_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {
    private Integer id;
    private Long price; // giá mặt hàng tại thời điểm mua hàng
    private Integer number; // số lượng
    private Long totalPrice; // tổng tiền
    private Long productId; // id sản phẩm
    private String orderId; // id đơn hàng
    private Long typeProductId; // loại mặt hàng
    private String image;
    private String nameProduct;
    private ProductDto productDto;
    private ListTypeProductDto listTypeProductDto;
}
