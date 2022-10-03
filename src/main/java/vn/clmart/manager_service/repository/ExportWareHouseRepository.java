package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.clmart.manager_service.model.ExportWareHouse;
import vn.clmart.manager_service.model.ImportWareHouse;

import java.util.Date;
import java.util.List;

public interface ExportWareHouseRepository extends JpaRepository<ExportWareHouse, Long> {

    public List<ExportWareHouse> findAllByDeleteFlgAndIdReceiptExportAndCompanyId(Integer delete, Long idReceiptExport, Long cid);

    public List<ExportWareHouse> findAllByIdReceiptExportAndCompanyId(Long idReceiptExport, Long cid);

    public List<ExportWareHouse> findAllByDeleteFlgAndIdItemsAndCompanyId(Integer delete, Long idItems, Long cid);

    @Query("select i from ExportWareHouse as i inner join ReceiptExportWareHouse as r on i.idReceiptExport = r.id where i.companyId = :cid and i.deleteFlg = :delete and ((lower(concat(coalesce(i.code, ''), coalesce(r.name, ''))) like lower(concat('%',coalesce(:search, ''), '%')))  or (coalesce(:search, '') = '')) and i.idReceiptExport is not null group by i.idReceiptExport order by i.createDate")
    public Page<ExportWareHouse> findAllByCompanyIdAndDeleteFlg(Long cid, Integer delete, String search, Pageable pageable);

    @Query("select i from ExportWareHouse as i where i.companyId = :cid " +
            " and i.deleteFlg = :delete " +
            " and  ((coalesce(i.createDate,current_date) between coalesce(:startDate, current_date) and coalesce(:endDate, current_date))) " +
            " and ((lower(concat(coalesce(i.code, ''),'')) like lower(concat('%',coalesce(:search, ''), '%')))  or (coalesce(:search, '') = '') ) " +
            " and (i.idReceiptExport is not null or  i.idOrder is not null) " +
            " group by i.idReceiptExport, i.idOrder " +
            " order by i.createDate")
    public Page<ExportWareHouse> statisticalByCompanyIdAndDeleteFlgAndOrder(Long cid, Integer delete, String search, Date startDate, Date endDate, Pageable pageable);

    public List<ExportWareHouse> findAllByDeleteFlgAndIdItemsAndCompanyIdAndIdReceiptImport(Integer deleteFlg, Long idItems, Long cid, Long idReceiptImport);

    public List<ExportWareHouse> findAllByDeleteFlgAndCompanyIdAndIdReceiptImport(Integer deleteFlg,Long cid, Long idReceiptImport);

    public List<ExportWareHouse> findAllByCompanyIdAndDeleteFlgAndIdOrder(Long cid, Integer deleteFlg, Long idOrder);
}
