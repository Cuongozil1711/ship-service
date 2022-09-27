package vn.clmart.manager_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ImportWareHouseResponseDTO {
    private Long id;
    private String code;
    private Integer numberBox;
    private Integer quantity;
    private Double totalPrice; // gia tien 1 item
    private Long idReceiptImport;
    private Long idItems;
    private Date createDate;
    private String itemsName = "";
    private String receiptImportName = "";
    private String creatByName = "";
}
