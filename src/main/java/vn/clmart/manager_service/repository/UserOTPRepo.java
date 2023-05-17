package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.UserOTP;

public interface UserOTPRepo extends JpaRepository<UserOTP, String> {
}
