package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.clmart.manager_service.model.Product;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product, Long> {
    @Query("select p from Product p inner join Category c on p.idCategory = c.id " +
            "where (lower(concat(coalesce(p.nameProduct, '') , coalesce(p.code, ''), coalesce(c.name, ''))) like " +
            "lower(concat('%', coalesce(:search, ''), '%')) " +
            "or coalesce(:search, '') = '') and (coalesce(:idCategory, 0l) = c.id or  coalesce(:idCategory, 0l) = 0l)")
    Page<Product> search(String search, Long idCategory, Pageable pageable);

    List<Product> findAllByIdIn(List<Long> ids);

    List<Product> findAllByDeleteFlgAndIdCategoryIn(Integer deleteFlg, List<Long> categoryIn);
}
