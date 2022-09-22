package vn.clmart.manager_service.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.config.exceptions.BusinessException;
import vn.clmart.manager_service.dto.CustomerDto;
import vn.clmart.manager_service.dto.ExportWareHouseDto;
import vn.clmart.manager_service.dto.OrderDto;
import vn.clmart.manager_service.dto.request.ItemsResponseDTO;
import vn.clmart.manager_service.dto.request.OrderItemResponseDTO;
import vn.clmart.manager_service.dto.request.OrderResponseDTO;
import vn.clmart.manager_service.model.ExportWareHouse;
import vn.clmart.manager_service.model.ImportWareHouse;
import vn.clmart.manager_service.model.Items;
import vn.clmart.manager_service.model.Order;
import vn.clmart.manager_service.repository.ItemsRepository;
import vn.clmart.manager_service.repository.OrderRepositorry;
import vn.clmart.manager_service.untils.Constants;

import java.util.*;

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

    @Autowired
    CustomerService customerService;

    public Order createOrder(OrderDto orderDto, Long cid, String uid){
        try {
            Order order = Order.of(orderDto, cid, uid);
            if(!orderRepositorry.findAllByCompanyIdAndDeleteFlgAndCode(cid, Constants.DELETE_FLG.NON_DELETE, orderDto.getCode()).isEmpty()){
                throw new BusinessException("Mã đã tồn tại");
            }
            // check so luong dat hang
            List<Map<String, Integer>> detailsItem = new ArrayList<>();
            for(Map<Long, Integer> items : orderDto.getDetailItems()){
                Map<String, Integer> dItems = new HashMap<>();
                Long idItems = Long.valueOf(items.keySet().stream().findFirst().get());
                Integer qualityItems = checkQualityItem(idItems, cid, uid);
                if(items.get(idItems) > qualityItems){
                    throw new BusinessException("Mặt hàng " + itemsService.getById(cid, uid, idItems).getName() + " không còn trong kho");
                }
                dItems.put(idItems.toString(), items.get(idItems));
                detailsItem.add(dItems);
            }
            order.setCode("OR" + orderDto.getDetailItems().size() + new Date().getTime());
            // dat hang
            order.setDetailItems(detailsItem);
            order = orderRepositorry.save(order);
            // luu vao xuat kho
            for(Map<String, Integer> items : detailsItem){
                Map<String, Integer> dItems = new HashMap<>();
                Long idItems = Long.valueOf(items.keySet().stream().findFirst().get());
                Integer qualityItems = items.get(idItems.toString());
                ExportWareHouseDto exportWareHouseDto = new ExportWareHouseDto();
                exportWareHouseDto.setQuantity(qualityItems);
                exportWareHouseDto.setIdReceiptExport(null);
                exportWareHouseDto.setIdItems(idItems);
                exportWareHouseDto.setTotalPrice(itemsService.getById(cid, uid, idItems).getPriceItem() * qualityItems);
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

    public OrderItemResponseDTO getOrderById(Long cid, Long id){
        try {
            Order order = orderRepositorry.findByCompanyIdAndId(cid, id).orElse(null);
            if(order != null){
                OrderItemResponseDTO itemsResponseDTO = new OrderItemResponseDTO();
                BeanUtils.copyProperties(order, itemsResponseDTO);
                List<Map<String, Integer>> orders = order.getDetailItems();
                Double totalPrice = 0d;
                Integer size = 0;
                List<ItemsResponseDTO> list = new ArrayList<>();
                for(int i=0; i < orders.size(); i++){
                    Map<String, Integer> item = orders.get(i);
                    Long idItems = Long.valueOf(item.keySet().stream().findFirst().get());
                    if(idItems != null){
                        Items items = itemsService.getById(cid, "", idItems);
                        ItemsResponseDTO itemsResponseDTO1 = new ItemsResponseDTO();
                        BeanUtils.copyProperties(items, itemsResponseDTO1);
                        Integer qualityItems = item.get(item.keySet().stream().findFirst().get());
                        itemsResponseDTO1.setTotalSold(qualityItems.longValue());
                        size +=qualityItems;
                        list.add(itemsResponseDTO1);
                        totalPrice += itemsService.getById(cid, "", idItems).getPriceItem() * qualityItems;
                    }
                }
                itemsResponseDTO.setItemsResponseDTOList(list);
                itemsResponseDTO.setTotalPrice(totalPrice);
                itemsResponseDTO.setQuantity(size);
                return itemsResponseDTO;
            }
            return null;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<Order> search(Long cid, Pageable pageable, String search){
        try {
            Page<Order> pageSearch = orderRepositorry.findAllByCompanyId(cid, search, pageable);
            List<Order> list = pageSearch.getContent();
            List<OrderResponseDTO> responseDTOList = new ArrayList<>();
            for(Order items : list){
                OrderResponseDTO itemsResponseDTO = new OrderResponseDTO();
                BeanUtils.copyProperties(items, itemsResponseDTO);
                if(items.getIdCustomer() != null){
                    CustomerDto customerDto = new CustomerDto();
                    BeanUtils.copyProperties(customerService.getById(cid , "", items.getIdCustomer()), customerDto);
                    itemsResponseDTO.setCustomerDto(customerDto);
                }
                Double totalPrice = 0d;
                Integer size = 0;
                for(int i=0; i < items.getDetailItems().size(); i++){
                    Map<String, Integer> orders = items.getDetailItems().get(i);
                    Long idItems = Long.valueOf(orders.keySet().stream().findFirst().get());
                    if(idItems != null){
                        Integer qualityItems = orders.get(orders.keySet().stream().findFirst().get());
                        size +=qualityItems;
                        totalPrice += itemsService.getById(cid, "", idItems).getPriceItem() * qualityItems;
                    }
                }
                itemsResponseDTO.setTotalPrice(totalPrice);
                itemsResponseDTO.setQuantity(size);

                responseDTOList.add(itemsResponseDTO);
            }
            return new PageImpl(responseDTOList, pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

}
