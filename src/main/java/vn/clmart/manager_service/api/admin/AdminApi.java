package vn.clmart.manager_service.api.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.dto.OrderDTO;
import vn.clmart.manager_service.service.OrderService;

@RestController
@RequestMapping("/admin")
public class AdminApi {
    private final OrderService orderService;

    public AdminApi(OrderService orderService) {
        this.orderService = orderService;
    }

    @PutMapping("/order/{id}")
    protected @ResponseBody
    ResponseEntity<Object> update(
            @RequestHeader String uid,
            @RequestBody OrderDTO orderDTO,
            @PathVariable("id") String id
    ) {
        try {
            return new ResponseEntity<>(orderService.updateOrder(uid, orderDTO, id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @PostMapping("/search-order")
    protected @ResponseBody
    ResponseEntity<Object> search(Pageable pageable) {
        try {
            return new ResponseEntity<>(orderService.findAll(pageable), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
