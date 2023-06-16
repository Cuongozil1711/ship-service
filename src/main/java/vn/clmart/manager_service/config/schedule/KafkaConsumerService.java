package vn.clmart.manager_service.config.schedule;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    @KafkaListener(topics = "cuongnv")
    public void consumerMessage(String message) {
        System.out.println("message="+message);
    }
}
