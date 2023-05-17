package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Order;

import java.util.List;

public interface OrderRepo extends JpaRepository<Order, String> {
    List<Order> findAllByCreateBy(String uid);
}
