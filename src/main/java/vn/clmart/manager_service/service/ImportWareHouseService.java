package vn.clmart.manager_service.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.config.exceptions.BusinessException;
import vn.clmart.manager_service.dto.ImportListDataWareHouseDto;
import vn.clmart.manager_service.dto.ImportWareHouseDto;
import vn.clmart.manager_service.dto.ItemsSearchDto;
import vn.clmart.manager_service.dto.request.ImportWareHouseResponseDTO;
import vn.clmart.manager_service.dto.request.ItemsResponseDTO;
import vn.clmart.manager_service.model.*;
import vn.clmart.manager_service.repository.ExportWareHouseRepository;
import vn.clmart.manager_service.repository.ImportWareHouseRepository;
import vn.clmart.manager_service.repository.ItemsRepository;
import vn.clmart.manager_service.repository.ReceiptImportWareHouseRepository;
import vn.clmart.manager_service.untils.Constants;
import vn.clmart.manager_service.untils.MapUntils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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

    @Autowired
    UserService userService;

    @Autowired
    ExportWareHouseRepository exportWareHouseRepository;

    @Autowired
    @Lazy
    ItemsService itemsService;



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
            String codeExport = "IM" + new Date().getTime();
            importWareHouse.setCode(codeExport);
            importWareHouse.setIdReceiptImport(importWareHouseDto.getIdReceiptImport());
            importWareHouseRepository.save(importWareHouse);
            return true;
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public boolean importListWareHouse(ImportListDataWareHouseDto importListDataWareHouseDto, Long cid, String uid){
        try {
            ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseService.getById(cid, uid, importListDataWareHouseDto.getIdReceiptImport());
            receiptImportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.COMPLETE.name());
            for(ImportWareHouseDto item : importListDataWareHouseDto.getData()){
                ImportWareHouse importWareHouse = ImportWareHouse.of(item, cid, uid);
                importWareHouse.setCode(importListDataWareHouseDto.getCode());
                importWareHouse.setIdReceiptImport(importListDataWareHouseDto.getIdReceiptImport());
                importWareHouseRepository.save(importWareHouse);
            }
            return true;
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public Boolean restoreImportWareHouse(Long cid, String uid, Long[] idReceiptImports){
        try {
            for(Long idReceiptImport : idReceiptImports){
                List<ImportWareHouse> importWareHouses = importWareHouseRepository.findAllByDeleteFlgAndIdReceiptImportAndCompanyId(Constants.DELETE_FLG.DELETE, idReceiptImport, cid);
                importWareHouses.forEach(importWareHouse -> {
                    importWareHouse.setDeleteFlg(Constants.DELETE_FLG.NON_DELETE);
                    importWareHouse.setUpdateBy(uid);
                });
                importWareHouseRepository.saveAll(importWareHouses);

                // chỉnh sửa lại trạng thái phiếu
                ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(idReceiptImport, cid, Constants.DELETE_FLG.NON_DELETE).orElse(null);
                if(receiptImportWareHouse != null){
                    receiptImportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.COMPLETE.name());
                    receiptImportWareHouse.setUpdateBy(uid);
                    receiptImportWareHouseRepository.save(receiptImportWareHouse);
                }
            }
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
        return true;
    }

    public boolean deleteExport(Long cid, String uid, Long idReceiptImport){
        try {
            List<ExportWareHouse> wareHouseList = exportWareHouseRepository.findAllByDeleteFlgAndCompanyIdAndIdReceiptImport(Constants.DELETE_FLG.NON_DELETE, cid, idReceiptImport);
            if(wareHouseList.isEmpty()){
                // chưa được dùng order hoặc xuất kho
                List<ImportWareHouse> importWareHouses = importWareHouseRepository.findAllByDeleteFlgAndIdReceiptImportAndCompanyId(Constants.DELETE_FLG.NON_DELETE, idReceiptImport, cid);
                importWareHouses.forEach(importWareHouse -> {
                    importWareHouse.setDeleteFlg(Constants.DELETE_FLG.DELETE);
                    importWareHouse.setUpdateBy(uid);
                    importWareHouseRepository.save(importWareHouse);
                });
                // chỉnh sửa lại trạng thái phiếu
                ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(idReceiptImport, cid, Constants.DELETE_FLG.NON_DELETE).orElse(null);
                if(receiptImportWareHouse != null){
                    receiptImportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.CANCELED.name());
                    receiptImportWareHouse.setUpdateBy(uid);
                    receiptImportWareHouseRepository.save(receiptImportWareHouse);
                }
                return true;
            }
            else{
                // không được xóa
                return false;
            }

        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public boolean checkRemoveListImport(Long cid, String uid, Long idImport){
        ImportWareHouse importWareHouse = importWareHouseRepository.findByCompanyIdAndIdAndDeleteFlg(cid, idImport, Constants.DELETE_FLG.NON_DELETE).orElse(null);
        if(importWareHouse != null){
            List<ExportWareHouse> wareHouseList = exportWareHouseRepository.findAllByDeleteFlgAndIdItemsAndCompanyIdAndIdReceiptImport(Constants.DELETE_FLG.NON_DELETE, importWareHouse.getIdItems(), cid, importWareHouse.getIdReceiptImport());
            if(wareHouseList != null){
                return false;
            }
        }
        return true;
    }

    @Transactional
    public boolean editImportListWareHouse(ImportListDataWareHouseDto importListDataWareHouseDto, Long cid, String uid){
        try {
            ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseService.getById(cid, uid, importListDataWareHouseDto.getIdReceiptImport());
            receiptImportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.COMPLETE.name());
            if(importListDataWareHouseDto.getIdReceiptImport() != null){
                List<ImportWareHouseDto> listDataEdit = importListDataWareHouseDto.getData();
                for(ImportWareHouseDto importWareHouseEdit : listDataEdit){
                    if(importWareHouseEdit.getId() != null){
                        // check số lượng đã được dùng trong kho hay chưa
                        List<ExportWareHouse> wareHouseList = exportWareHouseRepository.findAllByDeleteFlgAndIdItemsAndCompanyIdAndIdReceiptImport(Constants.DELETE_FLG.NON_DELETE, importWareHouseEdit.getIdItems(), cid, importWareHouseEdit.getIdReceiptImport());
                        AtomicReference<Integer> quality = new AtomicReference<>(0);
                        wareHouseList.forEach(exportWareHouse -> {
                            quality.updateAndGet(v -> v + exportWareHouse.getNumberBox() * exportWareHouse.getQuantity());
                        });
                        if(importWareHouseEdit.getQuantity() * importWareHouseEdit.getNumberBox() < quality.get()){
                           throw new BusinessException(quality.get() + " sản phẩm " + itemsService.getById(cid, uid, importWareHouseEdit.getId()).getName() + " đã được sử dụng");
                        }
                        else{
                            ImportWareHouse importWareHouse = importWareHouseRepository.findByCompanyIdAndIdAndDeleteFlg(cid, importWareHouseEdit.getId(), Constants.DELETE_FLG.NON_DELETE).orElse(null);
                            if(importWareHouse != null){
                                importWareHouse.setQuantity(importWareHouseEdit.getQuantity());
                                importWareHouse.setNumberBox(importWareHouseEdit.getNumberBox());
                                importWareHouse.setTotalPrice(importWareHouseEdit.getTotalPrice());
                                importWareHouse.setIdItems(importWareHouseEdit.getIdItems());
                                importWareHouse.setDateExpired(importWareHouseEdit.getDateExpired());
                                importWareHouseRepository.save(importWareHouse);
                            }
                        }
                    }
                    else{
                        ImportWareHouse importWareHouse = ImportWareHouse.of(importWareHouseEdit, cid, uid);
                        String codeExport = "I" + itemsRepository.findById(importWareHouseEdit.getIdItems()).get().getName().trim() + new Date().getTime();
                        importWareHouse.setCode(codeExport);
                        importWareHouse.setIdReceiptImport(importWareHouseEdit.getIdReceiptImport());
                        importWareHouseRepository.save(importWareHouse);
                    }
                }
            }
            return true;
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public boolean checkUseItemsOfReceiptImport(List<ImportWareHouseDto> listDataEdit, ImportWareHouse importWareHouse){
        for(ImportWareHouseDto importWareHouse1 : listDataEdit){
            if(Objects.equals(importWareHouse1.getId(), importWareHouse.getId())){
                return true;
            }
        }
        return false;
    }

    public ImportListDataWareHouseDto getImportByReceiptId(Long cid, String uid, Long idReceiptImport){
        try {
            ImportListDataWareHouseDto importListDataWareHouseDto = new ImportListDataWareHouseDto();
            if(idReceiptImport != null){
                ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseService.getById(cid, uid, idReceiptImport);
                importListDataWareHouseDto.setCode(receiptImportWareHouse.getCode());
                importListDataWareHouseDto.setIdReceiptImport(idReceiptImport);
                List<ImportWareHouseDto> data = new ArrayList<>();
                List<ImportWareHouse> list = importWareHouseRepository.findAllByIdReceiptImportAndCompanyId(idReceiptImport, cid);
                data = list.stream().map(importWareHouse -> of(importWareHouse)).collect(Collectors.toList());
                importListDataWareHouseDto.setData(data);
            }
            return importListDataWareHouseDto;
        }
        catch (Exception ex){
            throw new RuntimeException(ex.getMessage());
        }

    }

    public ImportWareHouseDto of(ImportWareHouse importWareHouse){
        ImportWareHouseDto importWareHouseDto = new ImportWareHouseDto();
        importWareHouseDto = MapUntils.copyWithoutAudit(importWareHouse, importWareHouseDto);
        importWareHouseDto.setId(importWareHouse.getId());
        return importWareHouseDto;
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
                if(importWareHouse.getNumberBox() == null) importWareHouse.setNumberBox(1);
                total += importWareHouse.getQuantity() * importWareHouse.getNumberBox();
            }
            return total;
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public PageImpl<ImportWareHouseResponseDTO> search(Long cid, Integer status, String search, ItemsSearchDto itemsSearchDto, Pageable pageable){
        try {
            Page<ImportWareHouse> pageSearch = importWareHouseRepository.findAllByCompanyIdAndDeleteFlg(cid, status, search, itemsSearchDto.getStartDate(), itemsSearchDto.getEndDate(),  pageable);
            List<ImportWareHouseResponseDTO> responseDTOS = new ArrayList<>();
            for(ImportWareHouse item : pageSearch.getContent()){
                ImportWareHouseResponseDTO importWareHouseResponseDTO = new ImportWareHouseResponseDTO();
                importWareHouseResponseDTO.setIdReceiptImport(item.getIdReceiptImport());
                if(importWareHouseResponseDTO.getIdReceiptImport() != null){
                    importWareHouseResponseDTO.setReceiptImportWareHouse(receiptImportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(importWareHouseResponseDTO.getIdReceiptImport(), cid, Constants.DELETE_FLG.NON_DELETE).orElse(new ReceiptImportWareHouse()));
                    AtomicReference<Double> totalPrice = new AtomicReference<>(0d);
                    List<ImportWareHouse> wareHouseList = importWareHouseRepository.findAllByDeleteFlgAndIdReceiptImportAndCompanyId(status, importWareHouseResponseDTO.getIdReceiptImport(), cid);
                    wareHouseList.forEach(
                             importWareHouse -> {
                                 if(importWareHouse.getTotalPrice() != null)
                                 totalPrice.updateAndGet(v -> v + importWareHouse.getTotalPrice());
                             }
                    );
                    importWareHouseResponseDTO.setQuantityItems(wareHouseList.size());
                    importWareHouseResponseDTO.setTotalPrice(totalPrice.get());
                    FullName fullName = userService.getFullName(cid, item.getCreateBy());
                    FullName fullNameUpDate = userService.getFullName(cid,item.getUpdateBy());
                    if(status == 0){
                        importWareHouseResponseDTO.setUpdateByName(fullNameUpDate.getFirstName() + " "  + fullNameUpDate.getLastName());
                        importWareHouseResponseDTO.setUpdateDate(item.getModifiedDate());
                    }
                    importWareHouseResponseDTO.setCreatByName(fullName.getFirstName() + " " + fullName.getLastName());
                    importWareHouseResponseDTO.setCreateDate(item.getCreateDate());
                }
                responseDTOS.add(importWareHouseResponseDTO);
            }
            return new PageImpl(responseDTOS, pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public List<ImportWareHouse> getByIdtems(Long cid, Long idItems){
        try {
            return importWareHouseRepository.findAllByDeleteFlgAndIdItemsAndCompanyIdOrderByDateExpiredAsc(Constants.DELETE_FLG.NON_DELETE, idItems, cid);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<ImportWareHouseResponseDTO> listImport(Long cid, Integer status, String search, ItemsSearchDto itemsSearchDto, Pageable pageable){
        try {
            Page<ImportWareHouse> pageSearch = importWareHouseRepository.listImportWareHouse(cid, status, search, itemsSearchDto.getStartDate(), itemsSearchDto.getEndDate(),  pageable);
            List<ImportWareHouseResponseDTO> responseDTOS = new ArrayList<>();
            for(ImportWareHouse item : pageSearch.getContent()){
                ImportWareHouseResponseDTO importWareHouseResponseDTO = new ImportWareHouseResponseDTO();
                BeanUtils.copyProperties(item, importWareHouseResponseDTO);
                importWareHouseResponseDTO.setIdReceiptImport(item.getIdReceiptImport());
                if(item.getIdItems() != null){
                    ItemsResponseDTO items = itemsService.getById(cid, "", item.getIdItems());
                    importWareHouseResponseDTO.setItemName(items.getName());
                }
                if(importWareHouseResponseDTO.getIdReceiptImport() != null){
                    importWareHouseResponseDTO.setReceiptImportWareHouse(receiptImportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(importWareHouseResponseDTO.getIdReceiptImport(), cid, Constants.DELETE_FLG.NON_DELETE).orElse(new ReceiptImportWareHouse()));
                    importWareHouseResponseDTO.setQuantityItems(item.getQuantity() * item.getNumberBox());
                    importWareHouseResponseDTO.setTotalPrice(item.getTotalPrice());
                    FullName fullName = userService.getFullName(cid, item.getCreateBy());
                    FullName fullNameUpDate = userService.getFullName(cid,item.getUpdateBy());
                    if(status == 0){
                        importWareHouseResponseDTO.setUpdateByName(fullNameUpDate.getFirstName() + " "  + fullNameUpDate.getLastName());
                        importWareHouseResponseDTO.setUpdateDate(item.getModifiedDate());
                    }
                    importWareHouseResponseDTO.setCreatByName(fullName.getFirstName() + " " + fullName.getLastName());
                    importWareHouseResponseDTO.setCreateDate(item.getCreateDate());
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
