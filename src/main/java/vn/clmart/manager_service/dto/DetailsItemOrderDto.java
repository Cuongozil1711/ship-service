package vn.clmart.manager_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.clmart.manager_service.dto.request.ItemsResponseDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DetailsItemOrderDto {
    private String dvtCode;
    private Integer quality;
    private Long totalSale; // gia tien theo don vi tinh * quality thuc te
    private Long totalPrice; // gia tien theo don vi tinh * quality (gia goc)
    private Long idItems;
    private Long idReceiptImport;
    private Long idImportWareHouse;
    private Long idOrder;
    private Long idPromotion;
    private ItemsResponseDTO itemsResponseDTO;
}
