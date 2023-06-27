package vn.clmart.manager_service.config.schedule;

import io.vertx.core.json.JsonObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import vn.clmart.manager_service.dto.MessageDto;
import vn.clmart.manager_service.dto.kafka.EventRequest;

@Service
public class KafkaConsumerService {
    @KafkaListener(topics = "cuongnv_topics")
    public void consumerMessage(EventRequest eventRequest) {
        System.out.println("message=" + (MessageDto) eventRequest.getPayload());
    }
}
