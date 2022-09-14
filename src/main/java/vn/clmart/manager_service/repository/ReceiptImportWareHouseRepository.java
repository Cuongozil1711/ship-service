package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.ReceiptExportWareHouse;
import vn.clmart.manager_service.model.ReceiptImportWareHouse;

import java.util.Optional;

public interface ReceiptImportWareHouseRepository extends JpaRepository<ReceiptImportWareHouse, Long> {
    Optional<ReceiptImportWareHouse> findByIdAndCompanyIdAndDeleteFlg(Long id, Long cid, Integer deleteFlg);

    Page<ReceiptImportWareHouse> findAllByCompanyIdAndDeleteFlg(Long cid, Integer deleteFlg, Pageable pageable);
}
