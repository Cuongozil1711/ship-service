package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Category;
import vn.clmart.manager_service.model.Items;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndCompanyIdAndDeleteFlg(Long id, Long cid, Integer deleteFlg);


    Page<Category> findAllByCompanyIdAndDeleteFlg(Long cid, Integer deleteFlg, Pageable pageable);

}
