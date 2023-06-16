package vn.clmart.manager_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.config.exceptions.BusinessException;
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

public interface OrderService {
    OrderDTO createOrder(String uid, OrderDTO orderDTO);
    OrderDTO cancelOrder(String uid, String idOrder);
    OrderDTO updateOrder(String uid, OrderDTO orderDTO, String id);
    List<OrderDTO> getAllByIds(String uid);
    Page<OrderDTO> findAll(Pageable pageable);
}
