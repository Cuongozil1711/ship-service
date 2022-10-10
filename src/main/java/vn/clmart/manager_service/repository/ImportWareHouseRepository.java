package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.clmart.manager_service.model.ImportWareHouse;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ImportWareHouseRepository extends JpaRepository<ImportWareHouse, Long> {

    public List<ImportWareHouse> findAllByDeleteFlgAndIdReceiptImportAndCompanyId(Integer delete, Long idReceiptImport, Long cid);

    public List<ImportWareHouse> findAllByIdReceiptImportAndCompanyId(Long idReceiptImport, Long cid);


    public List<ImportWareHouse> findAllByDeleteFlgAndIdItemsAndCompanyId(Integer delete, Long idItems, Long cid);

    public List<ImportWareHouse> findAllByDeleteFlgAndIdItemsAndCompanyIdOrderByDateExpiredAsc(Integer delete, Long idItems, Long cid);

    public Optional<ImportWareHouse> findByCompanyIdAndIdAndDeleteFlg(Long cid, Long id, Integer deteteFlg);
    public Optional<ImportWareHouse> findByCompanyIdAndIdReceiptImportAndIdItemsAndDeleteFlg(Long cid, Long idReceiptImport, Long idItems, Integer deteteFlg);


    @Query("select i from ImportWareHouse as i inner join ReceiptImportWareHouse r on r.id = i.idReceiptImport where i.companyId = :cid and i.deleteFlg = :delete and  (i.createDate between coalesce(:startDate, current_date) and coalesce(:endDate, current_date) or coalesce(:startDate, current_date) = coalesce(:endDate, current_date) ) and ((lower(concat(coalesce(i.code, ''), coalesce(r.name, ''))) like lower(concat('%',coalesce(:search, ''), '%')))  or (coalesce(:search, '') = '') ) group by i.idReceiptImport order by i.createDate")
    public Page<ImportWareHouse> findAllByCompanyIdAndDeleteFlg(Long cid, Integer delete, String search, Date startDate, Date endDate, Pageable pageable);

    @Query("select i from ImportWareHouse as i inner join ReceiptImportWareHouse r on r.id = i.idReceiptImport where i.companyId = :cid and i.deleteFlg = :delete and  (i.createDate between coalesce(:startDate, current_date) and coalesce(:endDate, current_date) or coalesce(:startDate, current_date) = coalesce(:endDate, current_date) ) and ((lower(concat(coalesce(i.code, ''), coalesce(r.name, ''))) like lower(concat('%',coalesce(:search, ''), '%')))  or (coalesce(:search, '') = '') ) order by i.createDate")
    public Page<ImportWareHouse> listImportWareHouse(Long cid, Integer delete, String search, Date startDate, Date endDate, Pageable pageable);
}
