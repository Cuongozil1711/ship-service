package vn.clmart.manager_service.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.model.District;
import vn.clmart.manager_service.model.Province;
import vn.clmart.manager_service.model.Wards;
import vn.clmart.manager_service.repository.DistrictRepo;
import vn.clmart.manager_service.repository.ProvinceRepo;
import vn.clmart.manager_service.repository.WardsRepo;

import java.util.*;

@Service
@Transactional
public class AddressService {

    private static final Logger logger = Logger.getLogger(AddressService.class);

    @Autowired
    ProvinceRepo provinceRepository;

    @Autowired
    DistrictRepo districtRepo;

    @Autowired
    WardsRepo wardsRepository;


    public List<Province> findAllProvince(){
        try {
            return provinceRepository.findAll();
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public List<District> findAllDistrict(){
        try {
            return districtRepo.findAll();
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }
    public List<District> findAllDistrictByProvince(Integer proviceId){
        try {
            return districtRepo.findAllByProvinceId(proviceId);
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public List<Wards> findAllWards(){
        try {
            return wardsRepository.findAll();
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }
    public List<Wards> findAllWardsByDistrictId(Integer districtId){
        try {
            return wardsRepository.findAllByDistrictId(districtId);
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

}
