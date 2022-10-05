package vn.clmart.manager_service.api.statistical;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import vn.clmart.manager_service.service.OrderService;
import vn.clmart.manager_service.service.StatisticalService;

@Controller
@RequestMapping("/statisticalApi")
@RequiredArgsConstructor
public class StatisticalApi {

    @Autowired
    OrderService orderService;

    @Autowired
    StatisticalService statisticalService;

    @GetMapping("/getCountOrder")
    protected @ResponseBody
    ResponseEntity<Object> getCount(
            @RequestHeader Long cid,
            @RequestHeader String uid
    ) {
        try {
            return new ResponseEntity<>(orderService.getEmpoyeeOrder(cid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/getItemOrder")
    protected @ResponseBody
    ResponseEntity<Object> getItemOrder(
            @RequestHeader Long cid,
            @RequestHeader String uid
    ) {
        try {
            return new ResponseEntity<>(orderService.getItemOrder(cid, uid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @GetMapping()
    protected @ResponseBody
    ResponseEntity<Object> getStatistical(
            @RequestHeader Long cid,
            @RequestHeader String uid
    ) {
        try {
            return new ResponseEntity<>(statisticalService.statistical(cid, uid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/chartInfo")
    protected @ResponseBody
    ResponseEntity<Object> chartInfo(
            @RequestHeader Long cid,
            @RequestHeader String uid
    ) {
        try {
            return new ResponseEntity<>(statisticalService.getCountOrderAndImport(cid, uid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @GetMapping("/getOrderByEmployee")
    protected @ResponseBody
    ResponseEntity<Object> getOrderByEmployee(
            @RequestHeader Long cid,
            @RequestHeader String uid
    ) {
        try {
            return new ResponseEntity<>(statisticalService.getOrderByEmployee(cid, uid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }

    }
}
