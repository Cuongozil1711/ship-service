package vn.clmart.manager_service.service.implement;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.dto.PositionDto;
import vn.clmart.manager_service.model.Position;
import vn.clmart.manager_service.repository.PositionRepo;
import vn.clmart.manager_service.service.PositionService;
import vn.clmart.manager_service.utils.Constants;

@Service
@Transactional
public class PositionServiceImpl implements PositionService {
    private final PositionRepo positionRepository;

    public PositionServiceImpl(PositionRepo positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Override
    public Position create(PositionDto positionDto, Long cid, String uid){
        try {
            Position position = Position.of(cid, uid, positionDto);
            return  positionRepository.save(position);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Position update(PositionDto positionDto, Long cid, String uid, Long id){
        try {
            Position position = positionRepository.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            position.setUpdateBy(uid);
            position.setName(positionDto.getName());
            return positionRepository.save(position);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Position getById(Long id){
        try {
            return positionRepository.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElse(new Position());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
