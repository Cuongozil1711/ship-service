package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.ReviewProductDto;
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
@Description("Table đánh giá")
public class ReviewProduct extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id", strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Integer index;
    private String name;
    private String phone;
    private Integer star;
    private String review;
    private Long productId;

    public static ReviewProduct of(String uid, ReviewProductDto ReviewProductDto) {
        ReviewProduct reviewProduct = ReviewProduct.builder()
                .index(ReviewProductDto.getIndex())
                .name(ReviewProductDto.getName())
                .phone(ReviewProductDto.getPhone())
                .star(ReviewProductDto.getStar())
                .review(ReviewProductDto.getReview())
                .productId(ReviewProductDto.getProductId())
                .build();
        reviewProduct.setCreateBy(uid);
        reviewProduct.setUpdateBy(uid);
        return reviewProduct;
    }
}
