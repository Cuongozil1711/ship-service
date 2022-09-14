package vn.clmart.manager_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.config.exceptions.BusinessException;
import vn.clmart.manager_service.dto.ExportWareHouseDto;
import vn.clmart.manager_service.model.ExportWareHouse;
import vn.clmart.manager_service.model.ImportWareHouse;
import vn.clmart.manager_service.model.ReceiptExportWareHouse;
import vn.clmart.manager_service.repository.ExportWareHouseRepository;
import vn.clmart.manager_service.repository.ItemsRepository;
import vn.clmart.manager_service.repository.ReceiptExportWareHouseRepository;
import vn.clmart.manager_service.untils.Constants;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ExportWareHouseService {

    @Autowired
    ReceiptExportWareHouseService receiptExportWareHouseService;

    @Autowired
    ExportWareHouseRepository exportWareHouseRepository;

    @Autowired
    ItemsRepository itemsRepository;

    @Autowired
    ReceiptExportWareHouseRepository receiptExportWareHouseRepositoryr;

    public void validateDto(ExportWareHouseDto exportWareHouseDto, Long cid, String uid){
        if(exportWareHouseDto.getIdItems() == null){
            throw new BusinessException("Mặt hàng không được để trống");
        }

        if(exportWareHouseDto.getIdReceiptExport() == null){
            throw new BusinessException("Phiếu xuất kho không được để trống");
        }
        else{
            ReceiptExportWareHouse receiptExportWareHouse = receiptExportWareHouseService.getById(cid, uid, exportWareHouseDto.getIdReceiptExport());
            if(receiptExportWareHouse == null){
                throw new BusinessException("Không tìm thấy phiếu nhập kho");
            }
        }
    }

    public boolean exportWareHouse(ExportWareHouseDto exportWareHouseDto, Long cid, String uid){
        try {
            validateDto(exportWareHouseDto, cid, uid);
            ReceiptExportWareHouse receiptExportWareHouse = receiptExportWareHouseService.getById(cid, uid, exportWareHouseDto.getIdReceiptExport());
            receiptExportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.PROCESSING.name());
            ExportWareHouse exportWareHouse = ExportWareHouse.of(exportWareHouseDto, cid, uid);
            String codeExport = "E" + itemsRepository.findById(exportWareHouseDto.getIdItems()).get().getName().trim() + new Date();
            exportWareHouse.setCode(codeExport);
            exportWareHouseRepository.save(exportWareHouse);
            return true;
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public boolean orderToExport(ExportWareHouseDto exportWareHouseDto, Long cid, String uid){
        try {
            ExportWareHouse exportWareHouse = ExportWareHouse.of(exportWareHouseDto, cid, uid);
            String codeExport = "E" + itemsRepository.findById(exportWareHouseDto.getIdItems()).get().getName().trim() + new Date();
            exportWareHouse.setCode(codeExport);
            exportWareHouseRepository.save(exportWareHouse);
            return true;
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public List<ExportWareHouse> findAll(Long cid, String uid, Long idReceiptExport){
        try {
            ReceiptExportWareHouse receiptExportWareHouse = receiptExportWareHouseService.getById(cid, uid, idReceiptExport);
            if(receiptExportWareHouse == null){
                throw new BusinessException("Không tìm thấy phiếu nhập kho");
            }
            else{
                if(receiptExportWareHouse.getState().equals(Constants.RECEIPT_WARE_HOUSE.PROCESSING.name())){
                    throw new BusinessException("Phiếu nhập kho chưa hoàn thành");
                }
            }
            return exportWareHouseRepository.findAllByDeleteFlgAndIdReceiptExportAndCompanyId(Constants.DELETE_FLG.NON_DELETE, idReceiptExport, cid);
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public boolean completeWareHouse(Long cid, String uid, Long idReceiptExport){
        try {
            ReceiptExportWareHouse receiptExportWareHouse = receiptExportWareHouseService.getById(cid, uid, idReceiptExport);
            if(receiptExportWareHouse == null){
                throw new BusinessException("Không tìm thấy phiếu nhập kho");
            }
            else{
                List<ExportWareHouse> list = exportWareHouseRepository.findAllByDeleteFlgAndIdReceiptExportAndCompanyId(Constants.DELETE_FLG.NON_DELETE, idReceiptExport, cid);
                if(!list.isEmpty()){
                    receiptExportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.COMPLETE.name());
                    receiptExportWareHouseRepositoryr.save(receiptExportWareHouse);
                }
            }
            return true;
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public List<ExportWareHouse> findAllByItems(Long cid, String uid, Long idItems){
        try {
            return exportWareHouseRepository.findAllByDeleteFlgAndIdItemsAndCompanyId(Constants.DELETE_FLG.NON_DELETE, idItems, cid);
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public Long totalItemsInExport(Long idItem, Long cid){
        try {
            List<ExportWareHouse> exportWareHouses = exportWareHouseRepository.findAllByDeleteFlgAndIdItemsAndCompanyId(Constants.DELETE_FLG.NON_DELETE, idItem, cid);
            if(exportWareHouses.size() == 0) return 0l;
            Long total = 0l;
            for(ExportWareHouse exportWareHouse : exportWareHouses){
                if(exportWareHouse.getNumberBox() == 0 || exportWareHouse.getNumberBox() == null) exportWareHouse.setNumberBox(1);
                total += exportWareHouse.getQuantity() * exportWareHouse.getNumberBox();
            }
            return total;
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

}
