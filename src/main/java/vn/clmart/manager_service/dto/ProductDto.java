package vn.clmart.manager_service.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProductDto {
    private Long id;
    private String content;
    private String nameProduct;
    private Long idCategory;
    private String trademark;
    private Integer height;
    private Integer width;
    private Integer length;
    private Double weight;
    private String codeWeight;
    private Long madeInId;
    private Long expiredId;
    private String information;
    private String code;
    private String images[];
    private List<ReviewProductDto> review;
    private List<ListTypeProductDto> listTypeProduct;
    private List<TypeAttributeDto> listTypeAttribute;
    private Map<String, List<TypeAttributeDto>> listTypeAttributeRes;
}
