package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Promotion;
import vn.clmart.manager_service.model.Publisher;

import java.util.Optional;

public interface PromotionRepository  extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByIdAndCompanyIdAndDeleteFlg(Long id, Long cid, Integer deleteFlg);

    Page<Promotion> findAllByCompanyIdAndDeleteFlg(Long cid, Integer deleteFlg, Pageable pageable);
}
