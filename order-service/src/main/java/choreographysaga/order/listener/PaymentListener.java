package choreographysaga.order.listener;

import choreographysaga.common.event.PaymentCompletedEvent;
import choreographysaga.common.event.PaymentFailedEvent;
import choreographysaga.order.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static choreographysaga.common.event.EventTypes.PAYMENT_COMPLETED_EVENT;
import static choreographysaga.common.event.EventTypes.PAYMENT_FAILED_EVENT;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentListener {
    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @RetryableTopic(
            attempts = "4",
            backOff = @BackOff(delay = 1000, multiplier = 2),
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            exclude = {
                    JacksonException.class,
                    IllegalArgumentException.class,
                    EntityNotFoundException.class
            }
    )
    @KafkaListener(topics = "Payment.events", groupId = "order-service")
    public void onPaymentEvent(@Payload String payload,
                               @Header("eventType") String eventType,
                               @Header("id") String eventId) throws Exception {
        switch (eventType) {
            case PAYMENT_FAILED_EVENT -> {
                log.info("PaymentFailedEvent received: {}", payload);
                PaymentFailedEvent event = objectMapper.readValue(payload, PaymentFailedEvent.class);
                orderService.markAsPaymentFailed(event.orderId(), UUID.fromString(eventId), PAYMENT_FAILED_EVENT);
            }
            case PAYMENT_COMPLETED_EVENT -> {
                log.info("PaymentCompletedEvent recieved: {}", payload);
                PaymentCompletedEvent event = objectMapper.readValue(payload, PaymentCompletedEvent.class);
                orderService.markAsPaymentCompleted(event.orderId(), UUID.fromString(eventId), PAYMENT_COMPLETED_EVENT);
            }
            case null -> log.warn("Received event with null eventType, eventId: {}, ignoring", eventId);
            default -> log.debug("Ignoring eventType={}", eventType);
        }
    }

    @DltHandler
    public void handleDlt(@Payload String payload,
                          @Header(value = "eventType", required = false) String eventType,
                          @Header(value = "id", required = false) String eventId,
                          @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String error) {
        log.error("DLT received: type={}, id={}, error={}, payload={}", eventType, eventId, error, payload);
    }
}
