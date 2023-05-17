package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.TypeAttribute;

import java.util.List;

public interface TypeAttributeRepo extends JpaRepository<TypeAttribute, Long> {
    List<TypeAttribute> findAllByProductIdAndDeleteFlg(Long productId, Integer status);
}
