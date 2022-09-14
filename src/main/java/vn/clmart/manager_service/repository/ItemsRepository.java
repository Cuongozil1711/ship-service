package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.clmart.manager_service.model.ImportWareHouse;
import vn.clmart.manager_service.model.Items;
import vn.clmart.manager_service.model.Position;

import java.util.Optional;

public interface ItemsRepository extends JpaRepository<Items, Long> {

    Optional<Items> findByIdAndCompanyIdAndDeleteFlg(Long id, Long cid, Integer deleteFlg);


    @Query("select i from Items as i where i.companyId = :cid and i.deleteFlg = :deleteFlg and ((lower(concat(coalesce(i.code, ''), coalesce(i.name, ''))) like lower(concat('%',coalesce(:search, ''), '%')))  or (coalesce(:search, '') = '') ) ")
    Page<Items> findAllByCompanyIdAndDeleteFlg(Long cid, Integer deleteFlg, Pageable pageable, String search);


}
