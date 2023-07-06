package vn.clmart.manager_service.service.implement;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import vn.clmart.manager_service.service.PaymentService;

@Service
@Qualifier("momo")
public class PaymentMomoServiceImpl implements PaymentService {
    @Override
    public void pay() {
        System.out.println("Momo");
    }
}
