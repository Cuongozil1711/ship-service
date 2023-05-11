package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.ProductDto;
import vn.clmart.manager_service.model.config.PersistableEntity;

import javax.persistence.Column;
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
@Description("Table san pham")
public class Product extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id", strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    @Column(columnDefinition = "text")
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
    @Column(columnDefinition = "text")
    private String information;

    public static Product of(String uid, ProductDto productDto) {
        Product product = Product.builder()
                .content(productDto.getContent())
                .nameProduct(productDto.getNameProduct())
                .idCategory(productDto.getIdCategory())
                .trademark(productDto.getTrademark())
                .height(productDto.getHeight())
                .weight(productDto.getWeight())
                .length(productDto.getLength())
                .weight(productDto.getWeight())
                .codeWeight(productDto.getCodeWeight())
                .madeInId(productDto.getMadeInId())
                .expiredId(productDto.getExpiredId())
                .information(productDto.getInformation()).build();
        product.setCreateBy(uid);
        product.setUpdateBy(uid);
        return product;
    }
}
