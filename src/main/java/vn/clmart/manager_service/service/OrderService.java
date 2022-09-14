package vn.clmart.manager_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.config.exceptions.BusinessException;
import vn.clmart.manager_service.dto.ExportWareHouseDto;
import vn.clmart.manager_service.dto.OrderDto;
import vn.clmart.manager_service.model.ExportWareHouse;
import vn.clmart.manager_service.model.ImportWareHouse;
import vn.clmart.manager_service.model.Items;
import vn.clmart.manager_service.model.Order;
import vn.clmart.manager_service.repository.ItemsRepository;
import vn.clmart.manager_service.repository.OrderRepositorry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OrderService {

    @Autowired
    ExportWareHouseService exportWareHouseService;

    @Autowired
    ImportWareHouseService importWareHouseService;

    @Autowired
    OrderRepositorry orderRepositorry;

    @Autowired
    ItemsService itemsService;


    public Order createOrder(OrderDto orderDto, Long cid, String uid){
        try {
            Order order = Order.of(orderDto, cid, uid);
            // check so luong dat hang
            List<Map<String, Integer>> detailsItem = new ArrayList<>();
            for(Map<String, Integer> items : orderDto.getDetailItems()){
                Map<String, Integer> dItems = new HashMap<>();
                Long idItems = Long.valueOf(items.keySet().stream().findFirst().get());
                Integer qualityItems = checkQualityItem(idItems, cid, uid);
                if(items.get(idItems.toString()) > qualityItems){
                    throw new BusinessException("Mặt hàng " + itemsService.getById(cid, uid, idItems).getName() + " không còn trong kho");
                }
                dItems.put(idItems.toString(), items.get(idItems));
                detailsItem.add(dItems);
            }
            // dat hang
            order.setDetailItems(orderDto.getDetailItems());
            order = orderRepositorry.save(order);
            // luu vao xuat kho
            for(Map<String, Integer> items : orderDto.getDetailItems()){
                Map<String, Integer> dItems = new HashMap<>();
                Long idItems = Long.valueOf(items.keySet().stream().findFirst().get());
                Integer qualityItems = items.get(idItems.toString());
                ExportWareHouseDto exportWareHouseDto = new ExportWareHouseDto();
                exportWareHouseDto.setQuantity(qualityItems);
                exportWareHouseDto.setIdReceiptExport(null);
                exportWareHouseDto.setIdItems(idItems);
                exportWareHouseDto.setTotalPrice(itemsService.getById(cid, uid, idItems).getPriceItem());
                exportWareHouseService.orderToExport(exportWareHouseDto, cid, uid);
            }
            // tao hoa don
            return order;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Integer checkQualityItem(Long id, Long cid, String uid){
        Integer qualityImport = 0;
        List<ImportWareHouse> importWareHouseList = importWareHouseService.findAllByItems(cid, uid, id);
        for(ImportWareHouse importWareHouse : importWareHouseList){
            if(importWareHouse.getNumberBox() == 0) {
                qualityImport += importWareHouse.getQuantity();
            }
            else{
                qualityImport += importWareHouse.getQuantity() * importWareHouse.getNumberBox();
            }
        }
        Integer qualityExport = 0;
        List<ExportWareHouse> exportWareHouseList = exportWareHouseService.findAllByItems(cid, uid, id);
        for(ExportWareHouse exportWareHouse : exportWareHouseList){
            if(exportWareHouse.getNumberBox() == 0) {
                qualityExport += exportWareHouse.getQuantity();
            }
            else{
                qualityExport += exportWareHouse.getQuantity() * exportWareHouse.getNumberBox();
            }
        }
        return qualityImport - qualityExport;
    }

}
