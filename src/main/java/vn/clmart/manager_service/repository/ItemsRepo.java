package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Items;

public interface ItemsRepo extends JpaRepository<Items, Long> {
}
