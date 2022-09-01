package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
