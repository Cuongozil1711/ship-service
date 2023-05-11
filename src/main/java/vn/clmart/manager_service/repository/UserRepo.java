package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, String> {

    Optional<User> findUserByUidAndDeleteFlg(String uid, Integer deleteFlg);

    List<User> findAllByUsernameAndDeleteFlg(String userName, Integer deleteFlg);

    Optional<User> findByUidAndDeleteFlg(String id, Integer deleteFlg);
}
