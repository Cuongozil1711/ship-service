package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Employee;
import vn.clmart.manager_service.model.Stalls;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findAllByIdUserAndDeleteFlgAndCompanyId(String idUser, Integer deleteFlg, Long companyId);

    Page<Employee> findAllByCompanyId(Long cid, Pageable pageable);

    Optional<Employee> findByCompanyIdAndId(Long cid, Long id);

    List<Employee> findAllByDeleteFlgAndCompanyId(Integer deleteFlg, Long companyId);
}
