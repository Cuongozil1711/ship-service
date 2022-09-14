package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.ReceiptExportWareHouse;

import java.util.Optional;

public interface ReceiptExportWareHouseRepository extends JpaRepository<ReceiptExportWareHouse, Long> {
    Optional<ReceiptExportWareHouse> findByIdAndCompanyIdAndDeleteFlg(Long id, Long cid, Integer deleteFlg);

    Page<ReceiptExportWareHouse> findAllByCompanyIdAndDeleteFlg(Long cid, Integer deleteFlg, Pageable pageable);
}
