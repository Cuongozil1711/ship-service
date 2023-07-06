package vn.clmart.manager_service.dto.kafka;

import java.io.Serializable;

public class EventRequest<E> implements Serializable {
    String event;
    Long companyId;
    E payload;
    String responseTopic;

    protected EventRequest(EventRequestBuilder<E, ?, ?> b) {
        this.event = b.event;
        this.companyId = b.companyId;
        this.payload = b.payload;
        this.responseTopic = b.responseTopic;
    }

    public static <E> EventRequestBuilder<E, ?, ?> builder() {
        return new EventRequestBuilderImpl();
    }

    public String getEvent() {
        return this.event;
    }

    public Long getCompanyId() {
        return this.companyId;
    }

    public E getPayload() {
        return this.payload;
    }

    public String getResponseTopic() {
        return this.responseTopic;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public void setPayload(E payload) {
        this.payload = payload;
    }

    public void setResponseTopic(String responseTopic) {
        this.responseTopic = responseTopic;
    }

    public boolean equals(Object o) {   
        if (o == this) {
            return true;
        } else if (!(o instanceof EventRequest)) {
            return false;
        } else {
            EventRequest<?> other = (EventRequest)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label59: {
                    Object this$companyId = this.getCompanyId();
                    Object other$companyId = other.getCompanyId();
                    if (this$companyId == null) {
                        if (other$companyId == null) {
                            break label59;
                        }
                    } else if (this$companyId.equals(other$companyId)) {
                        break label59;
                    }

                    return false;
                }

                Object this$event = this.getEvent();
                Object other$event = other.getEvent();
                if (this$event == null) {
                    if (other$event != null) {
                        return false;
                    }
                } else if (!this$event.equals(other$event)) {
                    return false;
                }

                Object this$payload = this.getPayload();
                Object other$payload = other.getPayload();
                if (this$payload == null) {
                    if (other$payload != null) {
                        return false;
                    }
                } else if (!this$payload.equals(other$payload)) {
                    return false;
                }

                Object this$responseTopic = this.getResponseTopic();
                Object other$responseTopic = other.getResponseTopic();
                if (this$responseTopic == null) {
                    if (other$responseTopic != null) {
                        return false;
                    }
                } else if (!this$responseTopic.equals(other$responseTopic)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventRequest;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $companyId = this.getCompanyId();
        result = result * 59 + ($companyId == null ? 43 : $companyId.hashCode());
        Object $event = this.getEvent();
        result = result * 59 + ($event == null ? 43 : $event.hashCode());
        Object $payload = this.getPayload();
        result = result * 59 + ($payload == null ? 43 : $payload.hashCode());
        Object $responseTopic = this.getResponseTopic();
        result = result * 59 + ($responseTopic == null ? 43 : $responseTopic.hashCode());
        return result;
    }

    public String toString() {
        String var10000 = this.getEvent();
        return "EventRequest(event=" + var10000 + ", companyId=" + this.getCompanyId() + ", payload=" + this.getPayload() + ", responseTopic=" + this.getResponseTopic() + ")";
    }

    public EventRequest() {
    }

    public EventRequest(String event, Long companyId, E payload, String responseTopic) {
        this.event = event;
        this.companyId = companyId;
        this.payload = payload;
        this.responseTopic = responseTopic;
    }

    public abstract static class EventRequestBuilder<E, C extends EventRequest<E>, B extends EventRequestBuilder<E, C, B>> {
        private String event;
        private Long companyId;
        private E payload;
        private String responseTopic;

        public EventRequestBuilder() {
        }

        protected abstract B self();

        public abstract C build();

        public B event(String event) {
            this.event = event;
            return this.self();
        }

        public B companyId(Long companyId) {
            this.companyId = companyId;
            return this.self();
        }

        public B payload(E payload) {
            this.payload = payload;
            return this.self();
        }

        public B responseTopic(String responseTopic) {
            this.responseTopic = responseTopic;
            return this.self();
        }

        public String toString() {
            return "EventRequest.EventRequestBuilder(event=" + this.event + ", companyId=" + this.companyId + ", payload=" + this.payload + ", responseTopic=" + this.responseTopic + ")";
        }
    }

    private static final class EventRequestBuilderImpl<E> extends EventRequestBuilder<E, EventRequest<E>, EventRequestBuilderImpl<E>> {
        private EventRequestBuilderImpl() {
        }

        protected EventRequestBuilderImpl<E> self() {
            return this;
        }

        public EventRequest<E> build() {
            return new EventRequest(this);
        }
    }
}

