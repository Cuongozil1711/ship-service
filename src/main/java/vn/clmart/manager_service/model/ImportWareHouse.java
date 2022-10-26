package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.ImportWareHouseDto;
import vn.clmart.manager_service.dto.ReceiptImportWareHouseDto;
import vn.clmart.manager_service.model.config.PersistableEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Description("Table nhap kho")
public class ImportWareHouse extends PersistableEntity<Long> {
    @Id
    private Long id;
    private String code;
    private Integer numberBox;
    private Integer quantity;
    private Double totalPrice; // gia tien 1 item
    private Long idReceiptImport;
    private Long idItems;
    private Date dateExpired;

    public static ImportWareHouse of(ImportWareHouseDto importWareHouseDto, Long cid, String uid){
        ImportWareHouse receiptImportWareHouse = ImportWareHouse.builder()
                .quantity(importWareHouseDto.getQuantity())
                .totalPrice(importWareHouseDto.getTotalPrice())
                .numberBox(importWareHouseDto.getNumberBox())
                .idItems(importWareHouseDto.getIdItems())
                .dateExpired(importWareHouseDto.getDateExpired())
                .build();
        receiptImportWareHouse.setCreateBy(uid);
        receiptImportWareHouse.setCompanyId(cid);
        return receiptImportWareHouse;
    }
}
