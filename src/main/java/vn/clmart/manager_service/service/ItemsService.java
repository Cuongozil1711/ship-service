package vn.clmart.manager_service.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.dto.ItemsDto;
import vn.clmart.manager_service.dto.ItemsResponseDto;
import vn.clmart.manager_service.dto.PriceItemsDto;
import vn.clmart.manager_service.dto.request.ItemsResponseDTO;
import vn.clmart.manager_service.model.ImportWareHouse;
import vn.clmart.manager_service.model.Items;
import vn.clmart.manager_service.model.PriceItems;
import vn.clmart.manager_service.repository.ItemsRepository;
import vn.clmart.manager_service.repository.PriceItemsRepository;
import vn.clmart.manager_service.untils.Constants;
import vn.clmart.manager_service.untils.MapUntils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ItemsService {

    @Autowired
    ItemsRepository itemsRepository;

    @Autowired
    @Lazy
    ImportWareHouseService importWareHouseService;

    @Autowired
    ExportWareHouseService exportWareHouseService;

    @Autowired
    PriceItemsRepository priceItemsRepository;

    @Autowired
    ReceiptImportWareHouseService receiptImportWareHouseService;

    @Autowired
    PromotionService promotionService;

    public Items create(ItemsDto itemsDto, Long cid, String uid){
        try {
            Items items = Items.of(itemsDto, cid, uid);
            items = itemsRepository.save(items);
            for(PriceItemsDto priceItems : itemsDto.getPriceItemsDtos()){
                PriceItems priceItem = new PriceItems();
                priceItem.setPriceItems(itemsDto.getPriceItem().longValue());
                priceItem.setDvtCode(priceItems.getDvtCode());
                priceItem.setIdItems(items.getId());
                priceItem.setCompanyId(cid);
                priceItem.setQuality(priceItems.getQuality());
                priceItemsRepository.save(priceItem);
            }
            return items;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Items update(ItemsDto itemsDto, Long cid, String uid, Long id){
        try {
            Items item = itemsRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            MapUntils.copyWithoutAudit(itemsDto, item);
            item.setCompanyId(cid);
            item.setUpdateBy(uid);
            // xóa giá cũ
            List<PriceItems> priceItemOld = priceItemsRepository.findAllByCompanyIdAndIdItemsAndDeleteFlg(cid, item.getId(), Constants.DELETE_FLG.NON_DELETE);
            for(PriceItems priceItem : priceItemOld){
                priceItem.setDeleteFlg(Constants.DELETE_FLG.DELETE);
                priceItemsRepository.save(priceItem);
            }
            // cập nhật lại giá mới
            for(PriceItemsDto priceItems : itemsDto.getPriceItemsDtos()){
                PriceItems priceItem = new PriceItems();
                priceItem.setPriceItems(priceItems.getPriceItems());
                priceItem.setDvtCode(priceItems.getDvtCode());
                priceItem.setIdItems(item.getId());
                priceItem.setQuality(priceItems.getQuality());
                priceItem.setCompanyId(cid);
                priceItemsRepository.save(priceItem);
            }
            return itemsRepository.save(item);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public ItemsResponseDTO getById(Long cid, String uid, Long id){
        try {
            Items items = itemsRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            ItemsResponseDTO itemsResponseDTO = new ItemsResponseDTO();
            BeanUtils.copyProperties(items, itemsResponseDTO);
            List<PriceItems> priceItems = priceItemsRepository.findAllByCompanyIdAndIdItemsAndDeleteFlg(cid, items.getId(), Constants.DELETE_FLG.NON_DELETE);
            itemsResponseDTO.setPriceItemsDtos(priceItems.stream().map(e -> of(e)).collect(Collectors.toList()));
            return itemsResponseDTO;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PriceItemsDto of(PriceItems priceItems){
        PriceItemsDto priceItemsDto = new PriceItemsDto();
        BeanUtils.copyProperties(priceItems, priceItemsDto);
        return priceItemsDto;
    }

    public PageImpl<ItemsResponseDTO> search(Long cid, Pageable pageable, String search){
        try {
            Page<Items> pageSearch = itemsRepository.findAllByCompanyIdAndDeleteFlg(cid, Constants.DELETE_FLG.NON_DELETE, pageable, search);
            List<Items> list = pageSearch.getContent();
            List<ItemsResponseDTO> responseDTOList = new ArrayList<>();
            for(Items items : list){
                ItemsResponseDTO itemsResponseDTO = new ItemsResponseDTO();
                BeanUtils.copyProperties(items, itemsResponseDTO);
                itemsResponseDTO.setTotalInWareHouse(importWareHouseService.totalItemsInImport(itemsResponseDTO.getId(), cid) - exportWareHouseService.totalItemsInExport(itemsResponseDTO.getId(), cid));
                itemsResponseDTO.setTotalSold(exportWareHouseService.totalItemsInExport(itemsResponseDTO.getId(), cid));
                List<PriceItems> priceItems = priceItemsRepository.findAllByCompanyIdAndIdItemsAndDeleteFlg(cid, items.getId(), Constants.DELETE_FLG.NON_DELETE);
                itemsResponseDTO.setPriceItemsDtos(priceItems.stream().map(e -> of(e)).collect(Collectors.toList()));
                responseDTOList.add(itemsResponseDTO);
            }
            return new PageImpl(responseDTOList, pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public List<ItemsResponseDTO> list(Long cid){
        try {
            Page<Items> pageSearch = itemsRepository.findAllByCompanyIdAndDeleteFlg(cid, Constants.DELETE_FLG.NON_DELETE, PageRequest.of(0, Integer.MAX_VALUE), "");
            List<Items> list = pageSearch.getContent();
            List<ItemsResponseDTO> responseDTOList = new ArrayList<>();
            for(Items items : list){
                ItemsResponseDTO itemsResponseDTO = new ItemsResponseDTO();
                BeanUtils.copyProperties(items, itemsResponseDTO);
                itemsResponseDTO.setTotalInWareHouse(importWareHouseService.totalItemsInImport(itemsResponseDTO.getId(), cid) - exportWareHouseService.totalItemsInExport(itemsResponseDTO.getId(), cid));
                itemsResponseDTO.setTotalSold(exportWareHouseService.totalItemsInExport(itemsResponseDTO.getId(), cid));
                List<PriceItems> priceItems = priceItemsRepository.findAllByCompanyIdAndIdItemsAndDeleteFlg(cid, items.getId(), Constants.DELETE_FLG.NON_DELETE);
                itemsResponseDTO.setPriceItemsDtos(priceItems.stream().map(e -> of(e)).collect(Collectors.toList()));
                itemsResponseDTO.setPromotionResponseDto(promotionService.getByItemsId(cid, items.getId()));
                responseDTOList.add(itemsResponseDTO);
            }
            return responseDTOList;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Items delete(Long cid, String uid, Long id){
        try {
            Items items = itemsRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            items.setDeleteFlg(Constants.DELETE_FLG.DELETE);
            items.setUpdateBy(uid);
            return itemsRepository.save(items);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public List<ItemsResponseDto> getByIdtems(Long cid, String uid, Long idItems){
        try {
            List<ItemsResponseDto> itemsResponseDtos = new ArrayList<>();
            List<ImportWareHouse> importWareHouses = importWareHouseService.getByIdtems(cid, idItems);
            itemsResponseDtos = importWareHouses.stream().map(importWareHouse -> {
                ItemsResponseDto itemsResponseDTO = new ItemsResponseDto();
                itemsResponseDTO.setReceiptImportWareHouse(receiptImportWareHouseService.getById(cid, uid, importWareHouse.getIdReceiptImport()));
                itemsResponseDTO.setDateExpired(importWareHouse.getDateExpired());
                // Số lượng đã bán
                itemsResponseDTO.setQualityExport(exportWareHouseService.qualityExport(cid, importWareHouse.getIdReceiptImport(), idItems, 1));
                itemsResponseDTO.setQualityCanceled(exportWareHouseService.qualityExport(cid, importWareHouse.getIdReceiptImport(), idItems, 0));
                itemsResponseDTO.setQualityImport(importWareHouse.getNumberBox() * importWareHouse.getQuantity());
                return itemsResponseDTO;
            }).collect(Collectors.toList());
            return itemsResponseDtos;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    // tìm kiếm sản phẩm theo lô còn hàng
    public ItemsResponseDto getByIdtemsAndQuality(Long cid, String uid, Long idItems){
        try {
            List<ItemsResponseDto> itemsResponseDtos = new ArrayList<>();
            List<ImportWareHouse> importWareHouses = importWareHouseService.getByIdtems(cid, idItems);
            for(ImportWareHouse importWareHouse : importWareHouses){
                ItemsResponseDto itemsResponseDTO = new ItemsResponseDto();
                itemsResponseDTO.setReceiptImportWareHouse(receiptImportWareHouseService.getById(cid, uid, importWareHouse.getIdReceiptImport()));
                itemsResponseDTO.setDateExpired(importWareHouse.getDateExpired());
                // Số lượng đã bán
                Integer qualityExport = exportWareHouseService.qualityExport(cid, importWareHouse.getIdReceiptImport(), idItems, 1);
                Integer qualityCanceled = exportWareHouseService.qualityExport(cid, importWareHouse.getIdReceiptImport(), idItems, 0);
                Integer qualityImport = importWareHouse.getNumberBox() * importWareHouse.getQuantity();
                itemsResponseDTO.setQualityExport(qualityExport);
                itemsResponseDTO.setQualityCanceled(qualityCanceled);
                itemsResponseDTO.setQualityImport(qualityImport);
                if(qualityImport > qualityExport){
                    return itemsResponseDTO;
                }
            }
            return new ItemsResponseDto();
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

}
