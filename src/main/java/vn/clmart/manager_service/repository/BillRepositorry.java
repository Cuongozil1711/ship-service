package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Bill;

public interface BillRepositorry extends JpaRepository<Bill, Long> {
}
