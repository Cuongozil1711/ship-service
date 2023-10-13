package vn.soft.ship_service.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vn.soft.ship_service.service.ShipService;

@Controller
@RequestMapping("/api/ship")
@RequiredArgsConstructor
public class ShipApi {
    private final ShipService shipService;

    @GetMapping("/all")
    protected @ResponseBody
    ResponseEntity<Object> getAll() {
        try {
            shipService.listShip();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(ex,  HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
