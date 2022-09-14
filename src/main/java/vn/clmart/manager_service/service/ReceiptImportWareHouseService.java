package vn.clmart.manager_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.dto.ReceiptImportWareHouseDto;
import vn.clmart.manager_service.dto.StallsDto;
import vn.clmart.manager_service.model.ReceiptImportWareHouse;
import vn.clmart.manager_service.model.Stalls;
import vn.clmart.manager_service.repository.ReceiptImportWareHouseRepository;
import vn.clmart.manager_service.untils.Constants;

@Service
@Transactional
public class ReceiptImportWareHouseService {

    @Autowired
    ReceiptImportWareHouseRepository receiptImportWareHouseRepository;


    public ReceiptImportWareHouse create(ReceiptImportWareHouseDto receiptImportWareHouseDto, Long cid, String uid){
        try {
            ReceiptImportWareHouse receiptImportWareHouse = ReceiptImportWareHouse.of(receiptImportWareHouseDto, cid, uid);
            receiptImportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.INIT.name());
            return receiptImportWareHouseRepository.save(receiptImportWareHouse);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public ReceiptImportWareHouse update(ReceiptImportWareHouseDto receiptImportWareHouseDto, Long cid, String uid, Long id){
        try {
            ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            receiptImportWareHouse.setCompanyId(cid);
            receiptImportWareHouse.setUpdateBy(uid);
            receiptImportWareHouse.setIdWareHouse(receiptImportWareHouseDto.getIdWareHouse());
            receiptImportWareHouse.setName(receiptImportWareHouseDto.getName());
            receiptImportWareHouse.setCode(receiptImportWareHouseDto.getCode());
            receiptImportWareHouse.setDateImport(receiptImportWareHouseDto.getDateImport());
            receiptImportWareHouse.setTotalPrice(receiptImportWareHouseDto.getTotalPrice());
            return receiptImportWareHouseRepository.save(receiptImportWareHouse);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public ReceiptImportWareHouse updateState(String state, Long cid, String uid, Long id){
        try {
            ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            receiptImportWareHouse.setUpdateBy(uid);
            receiptImportWareHouse.setState(state);
            return receiptImportWareHouseRepository.save(receiptImportWareHouse);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public ReceiptImportWareHouse getById(Long cid, String uid, Long id){
        try {
            return receiptImportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<ReceiptImportWareHouse> search(Long cid, Pageable pageable){
        try {
            Page<ReceiptImportWareHouse> pageSearch = receiptImportWareHouseRepository.findAllByCompanyIdAndDeleteFlg(cid, Constants.DELETE_FLG.NON_DELETE, pageable);
            return new PageImpl(pageSearch.getContent(), pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public ReceiptImportWareHouse delete(Long cid, String uid, Long id){
        try {
            ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            receiptImportWareHouse.setDeleteFlg(Constants.DELETE_FLG.DELETE);
            receiptImportWareHouse.setCompanyId(cid);
            receiptImportWareHouse.setUpdateBy(uid);
            return receiptImportWareHouseRepository.save(receiptImportWareHouse);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

}
