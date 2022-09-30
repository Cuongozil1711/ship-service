package vn.clmart.manager_service.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.config.exceptions.BusinessException;
import vn.clmart.manager_service.dto.DetailsItemOrderDto;
import vn.clmart.manager_service.dto.ExportWareHouseDto;
import vn.clmart.manager_service.dto.ExportWareHouseListDto;
import vn.clmart.manager_service.dto.request.ExportWareHouseResponseDTO;
import vn.clmart.manager_service.dto.request.ImportWareHouseResponseDTO;
import vn.clmart.manager_service.model.*;
import vn.clmart.manager_service.repository.ExportWareHouseRepository;
import vn.clmart.manager_service.repository.ItemsRepository;
import vn.clmart.manager_service.repository.PriceItemsRepository;
import vn.clmart.manager_service.repository.ReceiptExportWareHouseRepository;
import vn.clmart.manager_service.untils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
    ReceiptExportWareHouseRepository receiptExportWareHouseRepository;

    @Autowired
    UserService userService;

    @Autowired
    PriceItemsRepository priceItemsRepository;

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

    public ExportWareHouseListDto getByIdReceiptExport(Long cid, String uid, Long idReceiptExport){
        try {
            ExportWareHouseListDto exportWareHouseListDto = new ExportWareHouseListDto();
            List<ExportWareHouse> exportWareHouses = exportWareHouseRepository.findAllByDeleteFlgAndIdReceiptExportAndCompanyId(Constants.DELETE_FLG.NON_DELETE, idReceiptExport, cid);
            List<DetailsItemOrderDto> detailsItemOrderDtoList = new ArrayList<>();
            String code = "";
            if(exportWareHouses != null) code = exportWareHouses.get(0).getCode();
            exportWareHouses.forEach(exportWareHouse -> {
                DetailsItemOrderDto detailsItemOrderDto = new DetailsItemOrderDto();
                detailsItemOrderDto.setIdItems(exportWareHouse.getIdItems());
                detailsItemOrderDto.setQuality(exportWareHouse.getNumberBox());
                detailsItemOrderDto.setDvtCode(exportWareHouse.getDvtCode());
                detailsItemOrderDto.setIdReceiptImport(exportWareHouse.getIdReceiptImport());
                detailsItemOrderDtoList.add(detailsItemOrderDto);
            });
            exportWareHouseListDto.setCode(code);
            exportWareHouseListDto.setIdReceiptExport(idReceiptExport);
            exportWareHouseListDto.setData(detailsItemOrderDtoList);
            return exportWareHouseListDto;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public void deleteExport(Long cid, String uid, Long idReceiptExport){
        try {
            List<ExportWareHouse> exportWareHouses = exportWareHouseRepository.findAllByDeleteFlgAndIdReceiptExportAndCompanyId(Constants.DELETE_FLG.NON_DELETE, idReceiptExport, cid);
            exportWareHouses.forEach(exportWareHouse -> {
                exportWareHouse.setDeleteFlg(Constants.DELETE_FLG.DELETE);
                exportWareHouse.setUpdateBy(uid);
                exportWareHouseRepository.save(exportWareHouse);
            });
            // chỉnh sửa lại trạng thái phiếu
            ReceiptExportWareHouse receiptExportWareHouse = receiptExportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(idReceiptExport, cid, Constants.DELETE_FLG.NON_DELETE).orElse(null);
            if(receiptExportWareHouse != null){
                receiptExportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.PROCESSING.name());
                receiptExportWareHouse.setUpdateBy(uid);
                receiptExportWareHouseRepository.save(receiptExportWareHouse);
            }
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public void deleteExportByOrderId(Long cid, String uid, Long orgId){
        try {
            List<ExportWareHouse> exportWareHouses = exportWareHouseRepository.findAllByCompanyIdAndDeleteFlgAndIdOrder(cid, Constants.DELETE_FLG.NON_DELETE, orgId);
            exportWareHouses.forEach(exportWareHouse -> {
                exportWareHouse.setDeleteFlg(Constants.DELETE_FLG.DELETE);
                exportWareHouse.setUpdateBy(uid);
            });
            exportWareHouseRepository.saveAll(exportWareHouses);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public boolean exportWareHouse(ExportWareHouseListDto exportWareHouseListDto, Long cid, String uid){
        try {
            ReceiptExportWareHouse receiptExportWareHouse = receiptExportWareHouseService.getById(cid, uid, exportWareHouseListDto.getIdReceiptExport());
            receiptExportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.COMPLETE.name());

            for(DetailsItemOrderDto itemExport : exportWareHouseListDto.getData()){
                ExportWareHouseDto exportWareHouseDto = new ExportWareHouseDto();
                exportWareHouseDto.setNumberBox(itemExport.getQuality());
                Long idItems = itemExport.getIdItems();
                PriceItems priceItems = priceItemsRepository.findByCompanyIdAndIdItemsAndDeleteFlgAndDvtCode(cid, idItems, Constants.DELETE_FLG.NON_DELETE, itemExport.getDvtCode()).orElse(null);
                exportWareHouseDto.setQuantity(priceItems.getQuality());
                exportWareHouseDto.setIdReceiptExport(exportWareHouseListDto.getIdReceiptExport());
                exportWareHouseDto.setIdReceiptImport(itemExport.getIdReceiptImport());
                exportWareHouseDto.setIdItems(idItems);
                exportWareHouseDto.setTotalPrice(priceItems.getQuality() * itemExport.getQuality() * priceItems.getPriceItems().doubleValue());
                exportWareHouseDto.setDvtCode(itemExport.getDvtCode());
                ExportWareHouse exportWareHouse = ExportWareHouse.of(exportWareHouseDto, cid, uid);
                exportWareHouse.setCode(exportWareHouseListDto.getCode());
                exportWareHouseRepository.save(exportWareHouse);
            }
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
                    receiptExportWareHouseRepository.save(receiptExportWareHouse);
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
                if(exportWareHouse.getNumberBox() == null) exportWareHouse.setNumberBox(1);
                total += exportWareHouse.getQuantity() * exportWareHouse.getNumberBox();
            }
            return total;
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public PageImpl<ExportWareHouseResponseDTO> search(Long cid, Pageable pageable){
        try {
            Page<ExportWareHouse> pageSearch = exportWareHouseRepository.findAllByCompanyIdAndDeleteFlg(cid, Constants.DELETE_FLG.NON_DELETE, pageable);
            List<ExportWareHouseResponseDTO> responseDTOS = new ArrayList<>();
            for(ExportWareHouse item : pageSearch.getContent()){
                ExportWareHouseResponseDTO exportWareHouseResponseDTO = new ExportWareHouseResponseDTO();
                exportWareHouseResponseDTO.setIdReceiptExport(item.getIdReceiptExport());
                if(item.getIdReceiptExport() != null){
                    exportWareHouseResponseDTO.setReceiptExportWareHouse(receiptExportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(item.getIdReceiptExport(), cid, Constants.DELETE_FLG.NON_DELETE).orElse(new ReceiptExportWareHouse()));
                    List<ExportWareHouse> wareHouseList = exportWareHouseRepository.findAllByDeleteFlgAndIdReceiptExportAndCompanyId(Constants.DELETE_FLG.NON_DELETE, item.getIdReceiptExport(), cid);
                    AtomicReference<Double> totalPrice = new AtomicReference<>(0d);
                    wareHouseList.forEach(
                            importWareHouse -> {
                                if(importWareHouse.getTotalPrice() != null)
                                    totalPrice.updateAndGet(v -> v + importWareHouse.getTotalPrice());
                            }
                    );
                    exportWareHouseResponseDTO.setQuantityItems(wareHouseList.size());
                    exportWareHouseResponseDTO.setTotalPrice(totalPrice.get());
                    FullName fullName = userService.getFullName(cid, exportWareHouseResponseDTO.getReceiptExportWareHouse().getCreateBy());
                    exportWareHouseResponseDTO.setCreatByName(fullName.getFirstName() + " " + fullName.getLastName());
                    exportWareHouseResponseDTO.setCreateDate(item.getCreateDate());
                }
                responseDTOS.add(exportWareHouseResponseDTO);
            }
            return new PageImpl(responseDTOS, pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

}
