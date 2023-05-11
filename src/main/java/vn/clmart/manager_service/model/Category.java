package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.CategoryDto;
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
@Description("Table loai mat hang")
public class Category extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String name;
    private Long parentId;

    public static Category of(CategoryDto categoryDto, String uid){
        Category category = Category.builder()
                .name(categoryDto.getName())
                .parentId(categoryDto.getParentId())
                .build();
        category.setCreateBy(uid);
        return category;
    }
}
