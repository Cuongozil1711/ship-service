package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import vn.clmart.manager_service.model.Order;

import java.util.Optional;
import java.util.List;

public interface OrderRepositorry extends JpaRepository<Order, Long> {

    @Query("select i from Order as i where i.companyId = :cid  and ((lower(concat(coalesce(i.code, ''), coalesce(i.name, ''))) like lower(concat('%',coalesce(:search, ''), '%')))  or (coalesce(:search, '') = '') ) ")
    Page<Order> findAllByCompanyId(Long cid, String search, Pageable pageable);

    Optional<Order> findByCompanyIdAndId(Long cid, Long id);

    List<Order> findAllByCompanyIdAndDeleteFlgAndCode(Long cid, Integer deleteFlg, String code);
}
