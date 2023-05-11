package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepo extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndDeleteFlg(Long id, Integer deleteFlg);


    List<Category> findAllByDeleteFlg(Integer deleteFlg);
}
