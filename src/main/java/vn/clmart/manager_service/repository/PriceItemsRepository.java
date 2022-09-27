package vn.clmart.manager_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.PriceItems;
import java.util.*;
public interface PriceItemsRepository extends JpaRepository<PriceItems, Long> {

    List<PriceItems> findAllByCompanyIdAndIdItemsAndDeleteFlg(Long cid, Long idItems, Integer deleteFlg);

    Optional<PriceItems> findByCompanyIdAndIdItemsAndDeleteFlgAndDvtCode(Long cid, Long idItems, Integer deleteFlg, String dvtCode);

}
