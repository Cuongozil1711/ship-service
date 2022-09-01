package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUidAndCompanyIdAndDeleteFlg(String uid, Long companyId, Integer deleteFlg);

    Optional<User> findUserByUsernameAndCompanyIdAndDeleteFlg(String userName, Long companyId, Integer deleteFlg);
}
