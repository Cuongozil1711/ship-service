package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.ImportWareHouse;

import java.util.List;

public interface ImportWareHouseRepository extends JpaRepository<ImportWareHouse, Long> {

    public List<ImportWareHouse> findAllByDeleteFlgAndIdReceiptImportAndCompanyId(Integer delete, Long idReceiptImport, Long cid);

    public List<ImportWareHouse> findAllByDeleteFlgAndIdItemsAndCompanyId(Integer delete, Long idItems, Long cid);

}
