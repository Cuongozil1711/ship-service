package vn.clmart.manager_service.config.kafka.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import vn.clmart.manager_service.config.kafka.service.EventPublish;
import vn.clmart.manager_service.dto.kafka.EventRequest;

@Service
@RequiredArgsConstructor
public class EventFactory {
    private final String SCHEDULE_TOPIC = "schedule_request";
    private final String TOPIC = "cuongnv_topics";
    private static ApplicationContext context;

    @Autowired
    private EventPublish eventPublish;

    public void publishSchedule(Long cid, EventRequest message) {
        publish(cid, SCHEDULE_TOPIC, message);
    }

    public void publish(Long cid, EventRequest message) {
        publish(cid, TOPIC, message);
    }

    public void publish(Long cid, String topic, EventRequest message) {
        eventPublish.publish(topic, cid, message);
    }
}
