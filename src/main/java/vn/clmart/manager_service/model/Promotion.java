package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.PromotionDto;
import vn.clmart.manager_service.dto.StallsDto;
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
@Description("Table khuyen mai")
public class Promotion extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code;
    private String name;
    private Integer percent;
    private Double price;

    public static Promotion of(PromotionDto promotionDto, Long cid, String uid){
        Promotion stalls = Promotion.builder()
                .code(promotionDto.getCode())
                .name(promotionDto.getName())
                .percent(promotionDto.getPercent())
                .price(promotionDto.getPrice()).build();
        stalls.setCreateBy(uid);
        stalls.setCompanyId(cid);
        return stalls;
    }
}
