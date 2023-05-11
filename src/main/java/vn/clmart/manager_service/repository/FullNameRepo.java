package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.FullName;

public interface FullNameRepo extends JpaRepository<FullName, Long> {
}
