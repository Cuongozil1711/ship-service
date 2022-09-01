package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Position;
import vn.clmart.manager_service.model.Supplier;

import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByIdAndCompanyIdAndDeleteFlg(Long id, Long cid, Integer deleteFlg);


    Page<Supplier> findAllByCompanyIdAndDeleteFlg(Long cid, Integer deleteFlg, Pageable pageable);
}
