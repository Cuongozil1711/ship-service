package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.clmart.manager_service.model.ImportWareHouse;

import java.util.List;
import java.util.Optional;

public interface ImportWareHouseRepository extends JpaRepository<ImportWareHouse, Long> {

    public List<ImportWareHouse> findAllByDeleteFlgAndIdReceiptImportAndCompanyId(Integer delete, Long idReceiptImport, Long cid);

    public List<ImportWareHouse> findAllByDeleteFlgAndIdItemsAndCompanyId(Integer delete, Long idItems, Long cid);

    public Optional<ImportWareHouse> findByCompanyIdAndIdAndDeleteFlg(Long cid, Long id, Integer deteteFlg);


    @Query("select i from ImportWareHouse as i where i.companyId = :cid and i.deleteFlg = :delete group by i.idReceiptImport order by i.createDate")
    public Page<ImportWareHouse> findAllByCompanyIdAndDeleteFlg(Long cid, Integer delete, Pageable pageable);
}
