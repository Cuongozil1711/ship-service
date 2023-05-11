package vn.clmart.manager_service.dto;

import lombok.Data;

@Data
public class ListTypeProductDto {
    private Long id;
    private String nameOne;
    private String valueOne;
    private String nameTwo;
    private String valueTwo;
    private Long number;
    private Long price;
    private Integer percent;
    private Long priceSale;
    private String keyIndex;
    private String image;
    private Long productId;
    private Integer parentIndex;
}
