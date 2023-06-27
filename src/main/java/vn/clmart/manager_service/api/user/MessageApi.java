package vn.clmart.manager_service.api.user;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.clmart.manager_service.config.kafka.event.EventFactory;
import vn.clmart.manager_service.dto.MessageDto;
import vn.clmart.manager_service.dto.kafka.EventRequest;

@RestController
@RequestMapping("/user/message")
public class MessageApi {

    @Value("${rabbitmp.exchange_name}")
    private String EXCHANGE_NAME;
    @Value("${rabbitmp.routing_key}")
    private String ROUTING_KEY;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private EventFactory eventFactory;
    @PostMapping("/send")
    public void sendMessage(@RequestBody MessageDto messageDto) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, messageDto.getMessageText());
    }

    @PostMapping("/send-kafka")
    public void sendMessageKafka(@RequestBody MessageDto messageDto) {
        EventRequest eventRequest = new EventRequest();
        eventRequest.setPayload(messageDto);
        eventRequest.setCompanyId(137l);
        eventFactory.publish(137l, eventRequest);
    }
}
