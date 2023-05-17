package vn.clmart.manager_service.dto;

import lombok.Data;

@Data
public class TypeAttributeDto {
    private Long id;
    private Integer keyIndex;
    private String value;
    private String name;
    private String typeCode;
}
