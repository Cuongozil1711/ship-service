package vn.clmart.manager_service.api.shophouse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.dto.ItemsDto;
import vn.clmart.manager_service.dto.OrderDto;
import vn.clmart.manager_service.service.ItemsService;

@RestController
@RequestMapping("/order")
public class OrderApi {

    private final vn.clmart.manager_service.service.OrderService orderService;

    public OrderApi(vn.clmart.manager_service.service.OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping()
    protected @ResponseBody
    ResponseEntity<Object> create(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody OrderDto orderDto
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return new ResponseEntity<>(objectMapper.writeValueAsString(orderService.createOrder(orderDto, cid, uid)), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/search")
    protected @ResponseBody
    ResponseEntity<Object> search(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestParam(value = "", required = false) String search
            , Pageable pageable) {
        try {
            return new ResponseEntity<>(orderService.search(cid, pageable, search), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("{id}")
    protected @ResponseBody
    ResponseEntity<Object> getById(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("id") Long id
            , Pageable pageable) {
        try {
            return new ResponseEntity<>(orderService.getOrderById(cid, id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping("{id}")
    protected @ResponseBody
    ResponseEntity<Object> create(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody Long id
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return new ResponseEntity<>(objectMapper.writeValueAsString(orderService.deleteOrder(cid, uid, id)), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
