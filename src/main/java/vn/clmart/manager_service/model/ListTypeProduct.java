package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.ListTypeProductDto;
import vn.clmart.manager_service.model.config.PersistableEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Description("Table danh sách phân loại sản phẩm")
public class ListTypeProduct extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
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
    private Long productId;
    private Integer parentIndex;

    public static ListTypeProduct of(String uid, ListTypeProductDto listTypeProductDto) {
        ListTypeProduct listTypeProduct = ListTypeProduct.builder()
                .nameOne(listTypeProductDto.getNameOne())
                .valueOne(listTypeProductDto.getValueOne())
                .nameTwo(listTypeProductDto.getNameTwo())
                .valueTwo(listTypeProductDto.getValueTwo())
                .number(listTypeProductDto.getNumber())
                .price(listTypeProductDto.getPrice())
                .percent(listTypeProductDto.getPercent())
                .priceSale(listTypeProductDto.getPriceSale())
                .keyIndex(listTypeProductDto.getKeyIndex())
                .productId(listTypeProductDto.getProductId())
                .parentIndex(listTypeProductDto.getParentIndex())
                .build();
        listTypeProduct.setCreateBy(uid);
        listTypeProduct.setUpdateBy(uid);
        return listTypeProduct;
    }
}
