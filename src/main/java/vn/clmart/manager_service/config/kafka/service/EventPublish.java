package vn.clmart.manager_service.config.kafka.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import vn.clmart.manager_service.dto.kafka.EventRequest;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import java.util.*;

@Service
public class EventPublish {
    @Value("${spring.application}")
    String application;
    public static final String EVENT_KEY = "event";
    public static final String EVENT_DEFAULT = "event";
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    static Set<String> serviceTopics = new HashSet();

    public EventPublish(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishCompany(String topic, String cid, Object message, String key) {
        if (cid != null) {
            this.publish(topic, Long.valueOf(cid), message, key);
        } else {
            this.publish(topic, (Long)null, (Object)message, key);
        }

    }

    public void publish(String topic, Long cid, Object message) {
        EventRequest eventRequest = new EventRequest();
        eventRequest.setCompanyId(cid);
        eventRequest.setPayload(message);
        eventRequest.setEvent("event");
        this.publish(topic, JsonObject.mapFrom(eventRequest).toBuffer().getBytes(), this.createUUIDKey());
    }

    public void publish(String topic, Long cid, Object message, String key) {
        EventRequest eventRequest = new EventRequest();
        eventRequest.setCompanyId(cid);
        eventRequest.setPayload(message);
        eventRequest.setEvent("event");
        this.publish(topic, JsonObject.mapFrom(eventRequest).toBuffer().getBytes(), key);
    }

    public void publish(String topic, Long cid, JsonObject message) {
        EventRequest eventRequest = (EventRequest)message.mapTo(EventRequest.class);
        eventRequest.setCompanyId(cid);
        this.publish(topic, JsonObject.mapFrom(eventRequest).toBuffer().getBytes(), this.createUUIDKey());
    }

    public void publish(String topic, Long cid, JsonObject message, String key) {
        EventRequest eventRequest = (EventRequest)message.mapTo(EventRequest.class);
        eventRequest.setCompanyId(cid);
        this.publish(topic, JsonObject.mapFrom(eventRequest).toBuffer().getBytes(), key);
    }

    public <E extends EventRequest> void publish(String topic, Long cid, E eventRequest) {
        eventRequest.setCompanyId(cid);
        this.publish(topic, JsonObject.mapFrom(eventRequest).toBuffer().getBytes(), eventRequest.getEvent(), this.createUUIDKey());
    }

    public <E extends EventRequest> void publish(String topic, Long cid, E eventRequest, String key) {
        eventRequest.setCompanyId(cid);
        this.publish(topic, JsonObject.mapFrom(eventRequest).toBuffer().getBytes(), eventRequest.getEvent(), key);
    }

    public void publish(String topic, Long cid, List<JsonObject> message, String key) {
        EventRequest eventRequest = new EventRequest();
        eventRequest.setCompanyId(cid);
        eventRequest.setPayload(message);
        eventRequest.setEvent("event");
        this.publish(topic, Json.encode(eventRequest).getBytes(), key);
    }

    public void publish(String topic, List<JsonObject> message) {
        this.publish(topic, Json.encode(message).getBytes());
    }

    public void publish(String topic, byte[] message) {
        this.publish(topic, message, this.createUUIDKey());
    }

    public void publish(String topic, byte[] message, String key) {
        this.publish(topic, message, "event", key);
    }

    public void publish(String topic, byte[] message, String event, String key) {
        try {
            MessageBuilder<byte[]> messageBuilder = MessageBuilder.withPayload(message);
            messageBuilder.setHeader("kafka_topic", topic);
            if (key == null || key.trim().isEmpty()) {
                key = this.createUUIDKey();
            }

            messageBuilder.setHeaderIfAbsent("kafka_messageKey", key);
            if (event == null || event.trim().isEmpty()) {
                event = "event";
            }

            messageBuilder.setHeaderIfAbsent("event", event);
            Message<byte[]> kafkaMessage = messageBuilder.build();
            this.kafkaTemplate.send(kafkaMessage);
            if (!this.serviceTopics.contains(topic)) {
                this.serviceTopics.add(topic);

                try {
                    EventRequest<Map<String, Object>> eventRequest = new EventRequest();
                    JsonObject payload = new JsonObject();
                    payload.put("service", this.application);
                    payload.put("topic", topic);
                    eventRequest.setPayload(payload.getMap());
                    eventRequest.setCompanyId(0L);
                    eventRequest.setEvent("register-service-topic");
                    this.publish("sys-service", 0L, eventRequest);
                } catch (Exception var9) {
                    var9.printStackTrace();
                }
            }
        } catch (Exception var10) {
            var10.printStackTrace();
        }

    }

    public String createUUIDKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
