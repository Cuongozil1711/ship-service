package vn.soft.ship_service.service.implement;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.soft.ship_service.service.ShipService;


@Service
@Transactional
@Log4j2
public class ShipServiceImpl implements ShipService {


    @Override
    public void listShip() {
        System.out.println("list ship order");
    }

    @Override
    public void updateState() {
        System.out.println("update state list");
    }
}
