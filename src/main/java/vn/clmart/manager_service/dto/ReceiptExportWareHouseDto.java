package vn.clmart.manager_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ReceiptExportWareHouseDto {
    private Date dateExport;
    private String state;
    private String name;
    private String code;
    private Double totalPrice;
    private Long idWareHouse;
}
