package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Storage;

import java.util.List;

public interface StorageRepo extends JpaRepository<Storage, Long> {
    List<Storage> findAllByRootIdInAndTypeCodeAndDeleteFlgAndEntityId(List<String> rootIds, String typeCode, Integer deleteFlg, Long entityId);
    List<Storage> findAllByRootIdInAndTypeCodeAndDeleteFlgAndEntityIdIn(List<String> rootIds, String typeCode, Integer deleteFlg, List<Long> entityId);
    List<Storage> findAllByRootIdInAndTypeCodeAndDeleteFlg(List<String> rootIds, String typeCode, Integer deleteFlg);
    List<Storage> findAllByRootIdInAndTypeCodeAndDeleteFlgAndLinkedIdNotInAndEntityId(List<String> rootIds, String typeCode, Integer deleteFlg, List<String> images, Long entityId);
}
