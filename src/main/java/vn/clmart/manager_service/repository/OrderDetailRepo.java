package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.OrderDetail;

import java.util.List;

public interface OrderDetailRepo extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findAllByIdIn(List<Integer> ids);
    List<OrderDetail> findAllByOrderIdIn(List<String> ids);
}
