package vn.clmart.manager_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.dto.PositionDto;
import vn.clmart.manager_service.model.Position;
import vn.clmart.manager_service.repository.PositionRepository;
import vn.clmart.manager_service.untils.Constants;

@Service
@Transactional
public class PositionService {

    @Autowired
    PositionRepository positionRepository;

    public Position create(PositionDto positionDto, Long cid, String uid){
        try {
            Position position = Position.of(cid, uid, positionDto);
            return  positionRepository.save(position);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Position update(PositionDto positionDto, Long cid, String uid, Long id){
        try {
            Position position = positionRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            position.setCompanyId(cid);
            position.setUpdateBy(uid);
            position.setName(positionDto.getName());
            return positionRepository.save(position);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Position getById(Long cid, String uid, Long id){
        try {
            return positionRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<Position> search(Long cid, Pageable pageable){
        try {
            Page<Position> pageSearch = positionRepository.findAllByCompanyIdAndDeleteFlg(cid, Constants.DELETE_FLG.NON_DELETE, pageable);
            return new PageImpl(pageSearch.getContent(), pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Position delete(Long cid, String uid, Long id){
        try {
            Position position = positionRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            position.setDeleteFlg(Constants.DELETE_FLG.NON_DELETE);
            position.setCompanyId(cid);
            position.setUpdateBy(uid);
            return positionRepository.save(position);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
