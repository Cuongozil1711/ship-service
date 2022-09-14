package vn.clmart.manager_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.dto.ReceiptExportWareHouseDto;
import vn.clmart.manager_service.model.ReceiptExportWareHouse;
import vn.clmart.manager_service.repository.ReceiptExportWareHouseRepository;
import vn.clmart.manager_service.untils.Constants;

@Service
@Transactional
public class ReceiptExportWareHouseService {

    @Autowired
    ReceiptExportWareHouseRepository ReceiptExportWareHouseRepository;


    public ReceiptExportWareHouse create(ReceiptExportWareHouseDto ReceiptExportWareHouseDto, Long cid, String uid){
        try {
            ReceiptExportWareHouse receiptExportWareHouse = ReceiptExportWareHouse.of(ReceiptExportWareHouseDto, cid, uid);
            receiptExportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.INIT.name());
            return ReceiptExportWareHouseRepository.save(receiptExportWareHouse);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public ReceiptExportWareHouse update(ReceiptExportWareHouseDto ReceiptExportWareHouseDto, Long cid, String uid, Long id){
        try {
            ReceiptExportWareHouse ReceiptExportWareHouse = ReceiptExportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            ReceiptExportWareHouse.setCompanyId(cid);
            ReceiptExportWareHouse.setUpdateBy(uid);
            ReceiptExportWareHouse.setIdWareHouse(ReceiptExportWareHouseDto.getIdWareHouse());
            ReceiptExportWareHouse.setName(ReceiptExportWareHouseDto.getName());
            ReceiptExportWareHouse.setCode(ReceiptExportWareHouseDto.getCode());
            ReceiptExportWareHouse.setDateExport(ReceiptExportWareHouseDto.getDateExport());
            ReceiptExportWareHouse.setTotalPrice(ReceiptExportWareHouseDto.getTotalPrice());
            return ReceiptExportWareHouseRepository.save(ReceiptExportWareHouse);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public ReceiptExportWareHouse updateState(String state, Long cid, String uid, Long id){
        try {
            ReceiptExportWareHouse ReceiptExportWareHouse = ReceiptExportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            ReceiptExportWareHouse.setUpdateBy(uid);
            ReceiptExportWareHouse.setState(state);
            return ReceiptExportWareHouseRepository.save(ReceiptExportWareHouse);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public ReceiptExportWareHouse getById(Long cid, String uid, Long id){
        try {
            return ReceiptExportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<ReceiptExportWareHouse> search(Long cid, Pageable pageable){
        try {
            Page<ReceiptExportWareHouse> pageSearch = ReceiptExportWareHouseRepository.findAllByCompanyIdAndDeleteFlg(cid, Constants.DELETE_FLG.NON_DELETE, pageable);
            return new PageImpl(pageSearch.getContent(), pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public ReceiptExportWareHouse delete(Long cid, String uid, Long id){
        try {
            ReceiptExportWareHouse ReceiptExportWareHouse = ReceiptExportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            ReceiptExportWareHouse.setDeleteFlg(Constants.DELETE_FLG.DELETE);
            ReceiptExportWareHouse.setCompanyId(cid);
            ReceiptExportWareHouse.setUpdateBy(uid);
            return ReceiptExportWareHouseRepository.save(ReceiptExportWareHouse);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

}
