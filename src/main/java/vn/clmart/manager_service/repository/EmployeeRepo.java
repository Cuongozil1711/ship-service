package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepo extends JpaRepository<Employee, Long> {
    List<Employee> findAllByIdUserAndDeleteFlg(String idUser, Integer deleteFlg);

    Page<Employee> findAll(Pageable pageable);


    Optional<Employee> findByIdUser(String uid);
}
