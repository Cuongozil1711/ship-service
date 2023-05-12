package vn.clmart.manager_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.config.exceptions.BusinessException;
import vn.clmart.manager_service.dto.ListTypeProductDto;
import vn.clmart.manager_service.dto.OrderDTO;
import vn.clmart.manager_service.dto.OrderDetailDTO;
import vn.clmart.manager_service.model.*;
import vn.clmart.manager_service.repository.ListTypeProductRepo;
import vn.clmart.manager_service.repository.OrderDetailRepo;
import vn.clmart.manager_service.repository.OrderRepo;
import vn.clmart.manager_service.repository.ProductRepo;
import vn.clmart.manager_service.utils.Constants;

import javax.persistence.EntityExistsException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    OrderDetailRepo orderDetailRepo;

    @Autowired
    ListTypeProductRepo listTypeProductRepo;

    @Autowired
    ProductRepo productRepo;

    @Autowired
    CloudinaryService cloudinaryService;


    public OrderDTO createOrder(String uid, OrderDTO orderDTO) {
        try {
            Order order = Order.of(orderDTO, uid);
            order.setState(Constants.TYPE_ORDER.INIT.name());
            order = orderRepo.save(order);

            List<Long> typeProductIds = orderDTO.getOrderDetailDTOList().stream().filter(orderDetail -> orderDetail.getTypeProductId() != null).map(OrderDetailDTO::getTypeProductId).collect(Collectors.toList());;
            List<ListTypeProduct> listTypeProducts = listTypeProductRepo.findAllByIdIn(typeProductIds);

            List<Long> productIds = orderDTO.getOrderDetailDTOList().stream().filter(orderDetail -> orderDetail.getProductId() != null).map(OrderDetailDTO::getProductId).collect(Collectors.toList());;
            List<Product> products = productRepo.findAllByIdIn(productIds);
            Map<Long, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, Function.identity(), (o, n) -> n));

            List<Storage> storageImage = cloudinaryService.getListStorageByEntityId(listTypeProducts.stream().map(ListTypeProduct::getKeyIndex).collect(Collectors.toList()), Constants.MODEL_IMAGE.TYPE_PRODUCT.name(), productIds);
            Map<String, Storage> imagesListTypeMap = storageImage.stream().collect(Collectors.toMap(Storage::getRootId, Function.identity(), (o, n) -> o));
            Map<Long, String> imageMapTypeProduct = new HashMap<>();
            listTypeProducts.stream().forEach(listTypeProduct -> {
                if (imagesListTypeMap.containsKey(listTypeProduct.getKeyIndex()) && !imageMapTypeProduct.containsKey(listTypeProduct.getId())) {
                    imageMapTypeProduct.put(listTypeProduct.getId(), imagesListTypeMap.get(listTypeProduct.getKeyIndex()).getLinkedId());
                }
            });

            Order finalOrder = order;
            List<OrderDetail> orderDetails = orderDTO.getOrderDetailDTOList().stream().map(orderDetailDTO -> {
                if(orderDetailDTO.getProductId() == null || orderDetailDTO.getTypeProductId() == null) throw new BusinessException("PRODUCT_NOT_EMPTY");
                OrderDetail orderDetail = OrderDetail.of(uid, orderDetailDTO);
                orderDetail.setOrderId(finalOrder.getId());
                orderDetail.setNameProduct(productMap.get(orderDetailDTO.getProductId()).getNameProduct());
                orderDetail.setImage(imageMapTypeProduct.get(orderDetailDTO.getTypeProductId()));
                return orderDetail;
            }).collect(Collectors.toList());
            orderDetailRepo.saveAll(orderDetails);

            return orderDTO;
        }
        catch(Exception ex){
            logger.error("CREATE_ORDER", ex);
            throw new BusinessException(ex.getMessage());
        }
    }

    public OrderDTO cancelOrder(String uid, String idOrder){
        try {
            Order order = orderRepo.findById(idOrder).orElseThrow(EntityExistsException::new);
            if(order.getState().equals(Constants.TYPE_ORDER.COMPLETE.name())) throw new BusinessException("");
            order.setState(Constants.TYPE_ORDER.CANCELED.name());
            order.setUpdateBy(uid);
            orderRepo.save(order);
            OrderDTO orderDTO = new OrderDTO();
            BeanUtils.copyProperties(order, orderDTO);
            return orderDTO;
        }
        catch (Exception ex){
            logger.error("CANCEL_ORDER", ex);
            throw new BusinessException(ex.getMessage());
        }
    }

    public OrderDTO updateOrder(String uid, OrderDTO orderDTO, String id) {
        try {
            Order order = orderRepo.findById(id).orElseThrow(EntityExistsException::new);
            BeanUtils.copyProperties(orderDTO, order);
            order.setUpdateBy(uid);
            orderRepo.save(order);

            List<OrderDetail> orderDetails = orderDetailRepo.findAllByIdIn(orderDTO.getOrderDetailDTOList().stream().filter(orderDetailDTO -> orderDetailDTO.getId() != null).map(OrderDetailDTO::getId).collect(Collectors.toList()));


            Order finalOrder = order;
            List<OrderDetail> orderDetailUpdate = orderDTO.getOrderDetailDTOList().stream().map(orderDetailDTO -> {
                OrderDetail orderDetail;
                if(orderDetailDTO.getId() != null) orderDetail = orderDetails.stream().filter(o -> o.getId().equals(orderDetailDTO.getId())).findFirst().orElseThrow(EntityExistsException::new);
                else orderDetail = OrderDetail.of(uid, orderDetailDTO);
                orderDetail.setOrderId(finalOrder.getId());
                orderDetail.setUpdateBy(uid);
                return orderDetail;
            }).collect(Collectors.toList());

            orderDetailRepo.saveAll(orderDetailUpdate);

            return orderDTO;
        }
        catch(Exception ex) {
            logger.error("UPDATE_ORDER", ex);
            throw new BusinessException(ex.getMessage());
        }
    }

    public List<OrderDTO> getAllByIds(String uid){
        try {
            List<Order> order = orderRepo.findAllByCreateBy(uid);
            List<String> orderIds = order.stream().map(Order::getId).collect(Collectors.toList());

            List<OrderDetail> orderDetails = orderDetailRepo.findAllByOrderIdIn(orderIds);
            Map<String, List<OrderDetail>> orderDetailsMap = orderDetails.stream().collect(Collectors.groupingBy(OrderDetail::getOrderId));

            List<OrderDTO> orderDTOS = order.stream().map(p -> {
                OrderDTO orderDTO = new OrderDTO();
                BeanUtils.copyProperties(p, orderDTO);
                if (orderDetailsMap.containsKey(p.getId()))
                    orderDTO.setOrderDetailDTOList(toDTOs(orderDetailsMap.get(p.getId())));
                return orderDTO;
            }).collect(Collectors.toList());

            return orderDTOS;
        }
        catch (Exception ex){
            logger.error("GET_ORDER_BY", ex);
            throw new BusinessException(ex.getMessage());
        }
    }

    public Page<OrderDTO> findAll(Pageable pageable) {
        try {
            Page<Order> page = orderRepo.findAll(pageable);
            List<String> orderIds = page.get().collect(Collectors.toList()).stream().map(Order::getId).collect(Collectors.toList());

            List<OrderDetail> orderDetails = orderDetailRepo.findAllByOrderIdIn(orderIds);
            Map<String, List<OrderDetail>> orderDetailsMap = orderDetails.stream().collect(Collectors.groupingBy(OrderDetail::getOrderId));

            List<OrderDTO> orderDTOS = page.get().collect(Collectors.toList()).stream().map(p -> {
                OrderDTO orderDTO = new OrderDTO();
                BeanUtils.copyProperties(p, orderDTO);
                if (orderDetailsMap.containsKey(p.getId())) {
                    orderDTO.setOrderDetailDTOList(toDTOs(orderDetailsMap.get(p.getId())));
                };
                return orderDTO;
            }).collect(Collectors.toList());

            return new PageImpl(orderDTOS, pageable, page.getTotalElements());
        } catch (Exception ex) {
            logger.error("SEARCH_ALL", ex);
            throw new BusinessException(ex.getMessage());
        }
    }

    public OrderDetailDTO toDTO(OrderDetail orderDetail) {
        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
        BeanUtils.copyProperties(orderDetail, orderDetailDTO);
        return orderDetailDTO;
    }

    public List<OrderDetailDTO> toDTOs(List<OrderDetail> orderDetails) {
        return orderDetails.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
