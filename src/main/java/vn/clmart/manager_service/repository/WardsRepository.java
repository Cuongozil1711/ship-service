package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Wards;

public interface WardsRepository extends JpaRepository<Wards, Integer> {
}
