package vn.clmart.manager_service.api.shophouse;

import com.fasterxml.jackson.databind.ObjectMapper;
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


}
