package vn.clmart.manager_service.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.model.District;
import vn.clmart.manager_service.model.Province;
import vn.clmart.manager_service.model.Wards;
import vn.clmart.manager_service.repository.DistrictRepo;
import vn.clmart.manager_service.repository.ProvinceRepo;
import vn.clmart.manager_service.repository.WardsRepo;

import java.util.*;


public interface AddressService {

    List<Province> findAllProvince();

    List<District> findAllDistrict();

    List<District> findAllDistrictByProvince(Integer provinceId);

    List<Wards> findAllWards();

    List<Wards> findAllWardsByDistrictId(Integer districtId);


    // có thể định nghĩa thêm các interface method sử dụng default (có thể @Override)
    default void log(String str){
        System.out.println("This method is default implementation" + str);
    }

    // tương tự có thể thêm các interface method sử dụng static (nhưng k thể @Override)
    static boolean isNull(String string) {
        System.out.println("Interface Null Check");

        return string == null ? true : "".equals(string) ? true : false;
    }
}
