package vn.clmart.manager_service.api.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.service.PaymentService;

@RestController
@RequestMapping("/user/payment")
public class PaymentApi {

    @Autowired
    @Qualifier("vnPay")
    private PaymentService paymentService;


    @GetMapping()
    protected @ResponseBody
    ResponseEntity<Object> pay() {
        try {
            paymentService.pay();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

}
