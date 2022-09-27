package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.ExportWareHouseDto;
import vn.clmart.manager_service.dto.ImportWareHouseDto;
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
@Description("Table xuat kho")
public class ExportWareHouse extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code;
    private String note;
    private Integer numberBox;
    private Integer quantity;
    private Double totalPrice;
    private Long idReceiptExport;
    private Long idItems;
    private String dvtCode;

    public static ExportWareHouse of(ExportWareHouseDto exportWareHouseDto, Long cid, String uid){
        ExportWareHouse exportWareHouse = ExportWareHouse.builder()
                .quantity(exportWareHouseDto.getQuantity())
                .totalPrice(exportWareHouseDto.getTotalPrice())
                .idReceiptExport(exportWareHouseDto.getIdReceiptExport())
                .idItems(exportWareHouseDto.getIdItems())
                .note(exportWareHouseDto.getNote())
                .dvtCode(exportWareHouseDto.getDvtCode())
                .build();
        exportWareHouse.setCreateBy(uid);
        exportWareHouse.setCompanyId(cid);
        return exportWareHouse;
    }
}
