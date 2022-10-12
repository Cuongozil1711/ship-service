package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Bill;

import java.util.Optional;

public interface BillRepositorry extends JpaRepository<Bill, Long> {
    Optional<Bill> findByCompanyIdAndDeleteFlgAndId(Long cid, Integer deleteFlg, Long id);
    Optional<Bill> findByCompanyIdAndDeleteFlgAndIdOrder(Long cid, Integer deleteFlg, Long idOrder);
    Optional<Bill> findByCompanyIdAndIdOrder(Long cid, Long idOrder);
}
