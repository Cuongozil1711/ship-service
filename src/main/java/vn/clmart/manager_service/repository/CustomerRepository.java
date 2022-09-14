package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Customer;
import vn.clmart.manager_service.model.Employee;
import vn.clmart.manager_service.model.Supplier;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByIdAndCompanyIdAndDeleteFlg(Long id, Long cid, Integer deleteFlg);

    Page<Customer> findAllByCompanyIdAndDeleteFlg(Long cid, Integer deleteFlg, Pageable pageable);

}
