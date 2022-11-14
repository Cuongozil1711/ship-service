package vn.clmart.manager_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.model.District;
import vn.clmart.manager_service.model.Province;
import vn.clmart.manager_service.model.Wards;
import vn.clmart.manager_service.repository.DistrictRepository;
import vn.clmart.manager_service.repository.ProvinceRepository;
import vn.clmart.manager_service.repository.WardsRepository;

import java.util.*;

@Service
@Transactional
public class AddressService {

    @Autowired
    ProvinceRepository provinceRepository;

    @Autowired
    DistrictRepository districtRepository;

    @Autowired
    WardsRepository wardsRepository;


    public List<Province> findAllProvince(){
        return provinceRepository.findAll();
    }

    public List<District> findAllDistrict(){
        return districtRepository.findAll();
    }
    public List<District> findAllDistrictByProvince(Integer proviceId){
        return districtRepository.findAllByProvinceId(proviceId);
    }

    public List<Wards> findAllWards(){
        return wardsRepository.findAll();
    }
    public List<Wards> findAllWardsByDistrictId(Integer districtId){
        return wardsRepository.findAllByDistrictId(districtId);
    }

}
