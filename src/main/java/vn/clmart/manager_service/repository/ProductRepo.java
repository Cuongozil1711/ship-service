package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Product;

public interface ProductRepo extends JpaRepository<Product, Long> {
}
