package vn.clmart.manager_service.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.config.exceptions.BusinessException;
import vn.clmart.manager_service.dto.ImportWareHouseDto;
import vn.clmart.manager_service.dto.request.ImportWareHouseResponseDTO;
import vn.clmart.manager_service.model.ImportWareHouse;
import vn.clmart.manager_service.model.Items;
import vn.clmart.manager_service.model.Position;
import vn.clmart.manager_service.model.ReceiptImportWareHouse;
import vn.clmart.manager_service.repository.ImportWareHouseRepository;
import vn.clmart.manager_service.repository.ItemsRepository;
import vn.clmart.manager_service.repository.ReceiptImportWareHouseRepository;
import vn.clmart.manager_service.untils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ImportWareHouseService {
    @Autowired
    ImportWareHouseRepository importWareHouseRepository;

    @Autowired
    ReceiptImportWareHouseService receiptImportWareHouseService;

    @Autowired
    ReceiptImportWareHouseRepository receiptImportWareHouseRepository;

    @Autowired
    ItemsRepository itemsRepository;

    public void validateDto(ImportWareHouseDto importWareHouseDto, Long cid, String uid){
        if(importWareHouseDto.getIdItems() == null){
            throw new BusinessException("Mặt hàng không được để trống");
        }

        if(importWareHouseDto.getIdReceiptImport() == null){
            throw new BusinessException("Phiếu nhập kho không được để trống");
        }
        else{
            ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseService.getById(cid, uid, importWareHouseDto.getIdReceiptImport());
            if(receiptImportWareHouse == null){
                throw new BusinessException("Không tìm thấy phiếu nhập kho");
            }
        }
    }

    public boolean importWareHouse(ImportWareHouseDto importWareHouseDto, Long cid, String uid){
        try {
            validateDto(importWareHouseDto, cid, uid);
            ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseService.getById(cid, uid, importWareHouseDto.getIdReceiptImport());
            receiptImportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.COMPLETE.name());
            ImportWareHouse importWareHouse = ImportWareHouse.of(importWareHouseDto, cid, uid);
            String codeExport = "I" + itemsRepository.findById(importWareHouseDto.getIdItems()).get().getName().trim() + new Date();
            importWareHouse.setCode(codeExport);
            importWareHouseRepository.save(importWareHouse);
            return true;
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public List<ImportWareHouse> findAll(Long cid, String uid, Long idReceiptImport){
        try {
            ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseService.getById(cid, uid, idReceiptImport);
            if(receiptImportWareHouse == null){
                throw new BusinessException("Không tìm thấy phiếu nhập kho");
            }
            else{
                if(receiptImportWareHouse.getState().equals(Constants.RECEIPT_WARE_HOUSE.PROCESSING.name())){
                    throw new BusinessException("Phiếu nhập kho chưa hoàn thành");
                }
            }
            return importWareHouseRepository.findAllByDeleteFlgAndIdReceiptImportAndCompanyId(Constants.DELETE_FLG.NON_DELETE, idReceiptImport, cid);
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public List<ImportWareHouse> findAllByItems(Long cid, String uid, Long idItems){
        try {
            return importWareHouseRepository.findAllByDeleteFlgAndIdItemsAndCompanyId(Constants.DELETE_FLG.NON_DELETE, idItems, cid);
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public boolean completeWareHouse(Long cid, String uid, Long idReceiptImport){
        try {
            ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseService.getById(cid, uid, idReceiptImport);
            if(receiptImportWareHouse == null){
                throw new BusinessException("Không tìm thấy phiếu nhập kho");
            }
            else{
                List<ImportWareHouse> list = importWareHouseRepository.findAllByDeleteFlgAndIdReceiptImportAndCompanyId(Constants.DELETE_FLG.NON_DELETE, idReceiptImport, cid);
                if(!list.isEmpty()){
                    receiptImportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.COMPLETE.name());
                    receiptImportWareHouseRepository.save(receiptImportWareHouse);
                }
            }
            return true;
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public Long totalItemsInImport(Long idItem, Long cid){
        try {
            List<ImportWareHouse> importWareHouses =importWareHouseRepository.findAllByDeleteFlgAndIdItemsAndCompanyId(Constants.DELETE_FLG.NON_DELETE, idItem, cid);
            if(importWareHouses.size() == 0) return 0l;
            Long total = 0l;
            for(ImportWareHouse importWareHouse : importWareHouses){
                if(importWareHouse.getNumberBox() == 0 || importWareHouse.getNumberBox() == null) importWareHouse.setNumberBox(1);
                total += importWareHouse.getQuantity() * importWareHouse.getNumberBox();
            }
            return total;
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public PageImpl<ImportWareHouse> search(Long cid, Pageable pageable){
        try {
            Page<ImportWareHouse> pageSearch = importWareHouseRepository.findAllByCompanyIdAndDeleteFlg(cid, Constants.DELETE_FLG.NON_DELETE, pageable);
            List<ImportWareHouseResponseDTO> responseDTOS = new ArrayList<>();
            for(ImportWareHouse item : pageSearch.getContent()){
                ImportWareHouseResponseDTO importWareHouseResponseDTO = new ImportWareHouseResponseDTO();
                BeanUtils.copyProperties(item, importWareHouseResponseDTO);
                if(importWareHouseResponseDTO.getIdReceiptImport() != null){
                    importWareHouseResponseDTO.setReceiptImportName(receiptImportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(importWareHouseResponseDTO.getIdReceiptImport(), cid, Constants.DELETE_FLG.NON_DELETE).orElse(new ReceiptImportWareHouse()).getName());
                }
                if(importWareHouseResponseDTO.getIdItems() != null){
                    importWareHouseResponseDTO.setItemsName(itemsRepository.findByIdAndCompanyIdAndDeleteFlg(importWareHouseResponseDTO.getIdItems(), cid, Constants.DELETE_FLG.NON_DELETE).orElse(new Items()).getName());
                }
                responseDTOS.add(importWareHouseResponseDTO);
            }
            return new PageImpl(responseDTOS, pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
