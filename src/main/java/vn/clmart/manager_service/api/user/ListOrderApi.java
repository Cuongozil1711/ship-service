package vn.clmart.manager_service.api.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.service.OrderService;

@RestController
@RequestMapping("/user/list-order")
public class ListOrderApi {

    private final OrderService orderService;

    public ListOrderApi(OrderService orderService) {
        this.orderService = orderService;
    }

    @PutMapping("{id}")
    protected @ResponseBody
    ResponseEntity<Object> cancelOrder(
            @RequestHeader String uid,
            @PathVariable("id") String id
    ) {
        try {
            return new ResponseEntity<>(orderService.cancelOrder(uid, id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("{id}")
    protected @ResponseBody
    ResponseEntity<Object> getListOrder(
            @PathVariable("id") String id
    ) {
        try {
            return new ResponseEntity<>(orderService.getAllByIds(id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
