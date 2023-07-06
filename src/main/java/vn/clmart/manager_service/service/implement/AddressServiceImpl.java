package vn.clmart.manager_service.service.implement;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.model.District;
import vn.clmart.manager_service.model.Province;
import vn.clmart.manager_service.model.Wards;
import vn.clmart.manager_service.repository.DistrictRepo;
import vn.clmart.manager_service.repository.ProvinceRepo;
import vn.clmart.manager_service.repository.WardsRepo;
import vn.clmart.manager_service.service.AddressService;

import java.util.List;

@Service
@Transactional
public class AddressServiceImpl implements AddressService{
    private static final Logger logger = Logger.getLogger(AddressServiceImpl.class);

    private final ProvinceRepo provinceRepository;

    private final DistrictRepo districtRepo;

    private final WardsRepo wardsRepository;

    public AddressServiceImpl(ProvinceRepo provinceRepository, DistrictRepo districtRepo, WardsRepo wardsRepository) {
        this.provinceRepository = provinceRepository;
        this.districtRepo = districtRepo;
        this.wardsRepository = wardsRepository;
    }

    @Override
    public List<Province> findAllProvince(){
        try {
            return provinceRepository.findAll();
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<District> findAllDistrict(){
        try {
            return districtRepo.findAll();
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<District> findAllDistrictByProvince(Integer provinceId){
        try {
            return districtRepo.findAllByProvinceId(provinceId);
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Wards> findAllWards(){
        try {
            return wardsRepository.findAll();
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Wards> findAllWardsByDistrictId(Integer districtId){
        try {
            return wardsRepository.findAllByDistrictId(districtId);
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void log(String string){
        System.out.println("MyClass logging::" + string);
    }
}
