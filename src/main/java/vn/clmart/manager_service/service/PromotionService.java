package vn.clmart.manager_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.dto.PromotionDto;
import vn.clmart.manager_service.model.Promotion;
import vn.clmart.manager_service.repository.PromotionRepository;
import vn.clmart.manager_service.untils.Constants;

@Service
@Transactional
public class PromotionService {

    @Autowired
    PromotionRepository PromotionRepository;

    public Promotion create(PromotionDto PromotionDto, Long cid, String uid){
        try {
            Promotion promotion = Promotion.of(PromotionDto, cid, uid);
            return PromotionRepository.save(promotion);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Promotion update(PromotionDto PromotionDto, Long cid, String uid, Long id){
        try {
            Promotion Promotion = PromotionRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            Promotion.setCompanyId(cid);
            Promotion.setUpdateBy(uid);
            Promotion.setCode(PromotionDto.getCode());
            Promotion.setName(PromotionDto.getName());
            Promotion.setPercent(PromotionDto.getPercent());
            Promotion.setPrice(PromotionDto.getPrice());
            return PromotionRepository.save(Promotion);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Promotion getById(Long cid, String uid, Long id){
        try {
            return PromotionRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<Promotion> search(Long cid, Pageable pageable){
        try {
            Page<Promotion> pageSearch = PromotionRepository.findAllByCompanyIdAndDeleteFlg(cid, Constants.DELETE_FLG.NON_DELETE, pageable);
            return new PageImpl(pageSearch.getContent(), pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Promotion delete(Long cid, String uid, Long id){
        try {
            Promotion Promotion = PromotionRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            Promotion.setDeleteFlg(Constants.DELETE_FLG.DELETE);
            Promotion.setCompanyId(cid);
            Promotion.setUpdateBy(uid);
            return PromotionRepository.save(Promotion);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

}
