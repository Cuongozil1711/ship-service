package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Order;

public interface OrderRepo extends JpaRepository<Order, Long> {
}
