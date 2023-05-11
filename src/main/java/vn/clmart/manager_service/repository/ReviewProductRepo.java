package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.ReviewProduct;

import java.util.List;

public interface ReviewProductRepo extends JpaRepository<ReviewProduct, Long> {
    List<ReviewProduct> findAllByProductId(Long productId);
}
