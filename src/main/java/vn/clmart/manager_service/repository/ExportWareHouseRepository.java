package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.ExportWareHouse;
import vn.clmart.manager_service.model.ImportWareHouse;

import java.util.List;

public interface ExportWareHouseRepository extends JpaRepository<ExportWareHouse, Long> {

    public List<ExportWareHouse> findAllByDeleteFlgAndIdReceiptExportAndCompanyId(Integer delete, Long idReceiptExport, Long cid);

    public List<ExportWareHouse> findAllByDeleteFlgAndIdItemsAndCompanyId(Integer delete, Long idItems, Long cid);

    public Page<ExportWareHouse> findAllByCompanyIdAndDeleteFlg(Long cid, Integer delete, Pageable pageable);
}
