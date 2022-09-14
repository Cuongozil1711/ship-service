package vn.clmart.manager_service.dto;

import lombok.Data;

@Data
public class PromotionDto {
    private String code;
    private String name;
    private Integer percent;
    private Double price;
}
