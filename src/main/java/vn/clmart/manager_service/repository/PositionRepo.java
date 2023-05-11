package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Position;

import java.util.Optional;

public interface PositionRepo extends JpaRepository<Position, Long> {
    Optional<Position> findByIdAndDeleteFlg(Long id, Integer deleteFlg);
    Optional<Position> findByCodeAndDeleteFlg(String code, Integer deleteFlg);

    Page<Position> findAllByDeleteFlg(Integer deleteFlg, Pageable pageable);

}
