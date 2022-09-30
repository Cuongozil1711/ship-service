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
import vn.clmart.manager_service.dto.DetailsItemOrderDto;
import vn.clmart.manager_service.dto.ExportWareHouseDto;
import vn.clmart.manager_service.dto.OrderDto;
import vn.clmart.manager_service.dto.request.ItemsResponseDTO;
import vn.clmart.manager_service.dto.request.OrderItemResponseDTO;
import vn.clmart.manager_service.dto.request.OrderResponseDTO;
import vn.clmart.manager_service.model.*;
import vn.clmart.manager_service.repository.DetailsItemOrderRepository;
import vn.clmart.manager_service.repository.OrderRepositorry;
import vn.clmart.manager_service.repository.PriceItemsRepository;
import vn.clmart.manager_service.untils.Constants;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    PriceItemsRepository priceItemsRepository;

    @Autowired
    DetailsItemOrderRepository detailsItemOrderRepository;

    public Order createOrder(OrderDto orderDto, Long cid, String uid){
        try {
            Order order = Order.of(orderDto, cid, uid);
            if(!orderRepositorry.findAllByCompanyIdAndDeleteFlgAndCode(cid, Constants.DELETE_FLG.NON_DELETE, orderDto.getCode()).isEmpty()){
                throw new BusinessException("Mã đã tồn tại");
            }
            order.setCode("OR" + orderDto.getDetailsItemOrders().size() + new Date().getTime());
            // dat hang
            order = orderRepositorry.save(order);
            // check so luong dat hang
            List<DetailsItemOrder> detailsItem = new ArrayList<>();
            for(DetailsItemOrderDto items : orderDto.getDetailsItemOrders()){
                items.setIdOrder(order.getId());
                DetailsItemOrder dItems = DetailsItemOrder.of(items, cid, uid);
                Long idItems = items.getIdItems();
                Integer qualityItems = checkQualityItem(idItems, cid, uid); // check trong kho xem còn bao nhiêu
                PriceItems priceItems = priceItemsRepository.findByCompanyIdAndIdItemsAndDeleteFlgAndDvtCode(cid, idItems, Constants.DELETE_FLG.NON_DELETE, items.getDvtCode()).orElse(null);
                Integer qualitySold = 0;
                if(priceItems == null){
                    throw new BusinessException("Không tìm " + itemsService.getById(cid, uid, idItems).getName() + " đơn vị tính");
                }
                else{
                    qualitySold = items.getQuality();
                }
                if(qualitySold * priceItems.getQuality() > qualityItems){
                    throw new BusinessException("Mặt hàng " + itemsService.getById(cid, uid, idItems).getName() + " không còn trong kho");
                }
                dItems.setTotalPrice(qualitySold * priceItems.getPriceItems());
                dItems.setQuality(qualitySold);
                dItems.setType("SOLD");
                detailsItem.add(dItems);
            }
            detailsItemOrderRepository.saveAll(detailsItem);
            // luu vao xuat kho
            for(DetailsItemOrderDto items : orderDto.getDetailsItemOrders()){
                Long idItems = items.getIdItems();
                PriceItems priceItems = priceItemsRepository.findByCompanyIdAndIdItemsAndDeleteFlgAndDvtCode(cid, idItems, Constants.DELETE_FLG.NON_DELETE, items.getDvtCode()).orElse(null);
                ExportWareHouseDto exportWareHouseDto = new ExportWareHouseDto();
                exportWareHouseDto.setNumberBox(items.getQuality());
                exportWareHouseDto.setQuantity(priceItems.getQuality());
                exportWareHouseDto.setIdReceiptExport(null);
                exportWareHouseDto.setIdItems(idItems);
                exportWareHouseDto.setIdReceiptImport(items.getIdReceiptImport());
                exportWareHouseDto.setDvtCode(items.getDvtCode());
                exportWareHouseDto.setIdOrder(order.getId());
                exportWareHouseDto.setTotalPrice(priceItems.getPriceItems() * items.getQuality().doubleValue());
                exportWareHouseService.orderToExport(exportWareHouseDto, cid, uid);
            }
            // tao hoa don
            return order;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public boolean deleteOrder(Long cid, String uid, Long id){
        Order order = orderRepositorry.findByCompanyIdAndIdAndDeleteFlg(cid, id, Constants.DELETE_FLG.NON_DELETE).orElse(null);
        if(order != null){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            Date dateNow = cal.getTime();
            if(order.getCreateDate().after(dateNow)){
                return false;
            }
            else{
                // update Order
                order.setDeleteFlg(Constants.DELETE_FLG.NON_DELETE);
                order.setUpdateBy(uid);
                orderRepositorry.save(order);

                // update price item don hang
                List<DetailsItemOrder> detailsItemOrders = detailsItemOrderRepository.findAllByCompanyIdAndDeleteFlgAndIdOrder(cid, Constants.DELETE_FLG.NON_DELETE, id);
                detailsItemOrders.forEach(detailsItemOrder -> {
                    detailsItemOrder.setDeleteFlg(Constants.DELETE_FLG.NON_DELETE);
                    detailsItemOrder.setUpdateBy(uid);
                });
                detailsItemOrderRepository.saveAll(detailsItemOrders);

                // check xuất kho để xóa
                exportWareHouseService.deleteExportByOrderId(cid, uid, order.getId());
            }
        }
        return false;
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
                List<DetailsItemOrder> orders = detailsItemOrderRepository.findAllByCompanyIdAndDeleteFlgAndIdOrder(cid, Constants.DELETE_FLG.NON_DELETE, id);
                Double totalPrice = 0d;
                Integer size = 0;
                List<DetailsItemOrderDto> list = new ArrayList<>();
                for(int i=0; i < orders.size(); i++){
                    DetailsItemOrderDto detailsItemOrderDto = new DetailsItemOrderDto();
                    DetailsItemOrder item = orders.get(i);
                    BeanUtils.copyProperties(item, detailsItemOrderDto);
                    if(item.getIdItems() != null){
                        ItemsResponseDTO itemsResponseDTO1 = itemsService.getById(cid, "", item.getIdItems());
                        PriceItems priceItems = priceItemsRepository.findByCompanyIdAndIdItemsAndDeleteFlgAndDvtCode(cid, item.getIdItems(), Constants.DELETE_FLG.NON_DELETE, item.getDvtCode()).orElse(null);
                        itemsResponseDTO1.setTotalSold(item.getQuality().longValue());
                        size += item.getQuality();
                        detailsItemOrderDto.setItemsResponseDTO(itemsResponseDTO1);
                        totalPrice += priceItems.getPriceItems() * item.getQuality();
                    }
                    list.add(detailsItemOrderDto);
                }
                itemsResponseDTO.setDetailsItemOrders(list);
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
                List<DetailsItemOrder> orders = detailsItemOrderRepository.findAllByCompanyIdAndDeleteFlgAndIdOrder(cid, Constants.DELETE_FLG.NON_DELETE, items.getId());
                for(int i=0; i < orders.size(); i++){
                    DetailsItemOrderDto detailsItemOrderDto = new DetailsItemOrderDto();
                    DetailsItemOrder item = orders.get(i);
                    BeanUtils.copyProperties(item, detailsItemOrderDto);
                    if(item.getIdItems() != null){
                        ItemsResponseDTO itemsResponseDTO1 = itemsService.getById(cid, "", item.getIdItems());
                        PriceItems priceItems = priceItemsRepository.findByCompanyIdAndIdItemsAndDeleteFlgAndDvtCode(cid, item.getIdItems(), Constants.DELETE_FLG.NON_DELETE, item.getDvtCode()).orElse(null);
                        itemsResponseDTO1.setTotalSold(item.getQuality().longValue());
                        size += item.getQuality();
                        detailsItemOrderDto.setItemsResponseDTO(itemsResponseDTO1);
                        totalPrice += priceItems.getPriceItems() * item.getQuality();
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

    public List<Map<String, Object>> getEmpoyeeOrder(Long cid){
        try {
            List<Map<String, Object>> listOrder = orderRepositorry.getOrdersByEmployee(cid, Constants.DELETE_FLG.NON_DELETE);
            return listOrder;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public List<OrderItemResponseDTO> getItemOrder(Long cid, String uid){
        try {
            List<Order> listOrder = orderRepositorry.getItemOrder(cid, uid, Constants.DELETE_FLG.NON_DELETE);
            return listOrder.stream().map(order -> getOrderById(cid, order.getId())).collect(Collectors.toList());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Long getRevenueNow(Long cid, String uid){
        try {
            Calendar calendar = new GregorianCalendar();
            List<Order> listOrderNow = orderRepositorry.getItemOrderByDateOrder(cid, calendar.getTime(), Constants.DELETE_FLG.NON_DELETE);
            return this.getRevenuePrice(listOrderNow, cid);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Long getRevenueAfter(Long cid, String uid){
        try {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.MONTH, -1);
            List<Order> listOrderAfter = orderRepositorry.getItemOrderByDateOrder(cid, calendar.getTime(), Constants.DELETE_FLG.NON_DELETE);
            return this.getRevenuePrice(listOrderAfter, cid);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    private Long getRevenuePrice(List<Order> orderList, Long cid){
        List<OrderItemResponseDTO> list = orderList.stream().map(order -> getOrderById(cid, order.getId())).collect(Collectors.toList());
        Long sumPrice = 0l;
        for(OrderItemResponseDTO orderItemResponseDTO : list){
            sumPrice += orderItemResponseDTO.getTotalPrice().longValue();
        }
        return sumPrice;
    }

}
