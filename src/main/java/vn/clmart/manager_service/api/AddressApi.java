package vn.clmart.manager_service.api;


import lombok.RequiredArgsConstructor;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.service.AddressService;
import java.util.HashMap;

@Controller
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressApi {

    @Autowired
    AddressService addressService;

    @GetMapping()
    protected @ResponseBody
    ResponseEntity<Object> province(
            ) {
        try {
            Map<String, Object> address = new HashMap<>();
            address.put("province", addressService.findAllProvince());
            address.put("district", addressService.findAllDistrict());
            address.put("wards", addressService.findAllWards());
            return new ResponseEntity<>(address, HttpStatus.OK);
        }
        catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

}
