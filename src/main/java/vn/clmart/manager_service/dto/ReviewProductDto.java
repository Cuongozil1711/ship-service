package vn.clmart.manager_service.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ReviewProductDto {
    private Long id;
    private Integer index;
    private String name;
    private String phone;
    private Integer star;
    private String review;
    private Date createDate;
    private Long productId;
}
