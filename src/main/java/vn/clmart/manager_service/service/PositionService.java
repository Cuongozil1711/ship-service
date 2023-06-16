package vn.clmart.manager_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.dto.PositionDto;
import vn.clmart.manager_service.model.Position;
import vn.clmart.manager_service.repository.PositionRepo;
import vn.clmart.manager_service.utils.Constants;


public interface PositionService {
    Position create(PositionDto positionDto, Long cid, String uid);
    Position update(PositionDto positionDto, Long cid, String uid, Long id);
    Position getById(Long id);
}
