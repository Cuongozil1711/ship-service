package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findUserByUidAndCompanyIdAndDeleteFlg(String uid, Long companyId, Integer deleteFlg);

    List<User> findAllByUsernameAndCompanyIdAndDeleteFlg(String userName, Long companyId, Integer deleteFlg);

    Optional<User> findByCompanyIdAndUidAndDeleteFlg(Long companyId, String id, Integer deleteFlg);
}
