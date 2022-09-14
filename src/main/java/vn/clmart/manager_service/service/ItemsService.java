package vn.clmart.manager_service.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.dto.ItemsDto;
import vn.clmart.manager_service.dto.request.ItemsResponseDTO;
import vn.clmart.manager_service.model.Items;
import vn.clmart.manager_service.repository.ItemsRepository;
import vn.clmart.manager_service.untils.Constants;
import vn.clmart.manager_service.untils.MapUntils;

import java.util.ArrayList;
import java.util.List;
@Service
@Transactional
public class ItemsService {

    @Autowired
    ItemsRepository itemsRepository;

    @Autowired
    ImportWareHouseService importWareHouseService;

    @Autowired
    ExportWareHouseService exportWareHouseService;

    public Items create(ItemsDto itemsDto, Long cid, String uid){
        try {
            Items items = Items.of(itemsDto, cid, uid);
            return itemsRepository.save(items);
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
            return itemsRepository.save(item);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Items getById(Long cid, String uid, Long id){
        try {
            return itemsRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
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
                responseDTOList.add(itemsResponseDTO);
            }
            return new PageImpl(responseDTOList, pageable, pageSearch.getTotalElements());
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

}
