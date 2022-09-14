package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;
import vn.clmart.manager_service.model.Order;

public interface OrderRepositorry extends JpaRepository<Order, Long> {
}
