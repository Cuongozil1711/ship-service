package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.ListTypeProduct;

import java.util.List;

public interface ListTypeProductRepo extends JpaRepository<ListTypeProduct, Long> {
    List<ListTypeProduct> findAllByProductId(Long productId);
}
