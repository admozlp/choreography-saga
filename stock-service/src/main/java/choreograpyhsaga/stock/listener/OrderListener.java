package choreograpyhsaga.stock.listener;

import choreographysaga.common.event.OrderStatusUpdateFailedEvent;
import choreographysaga.common.event.PaymentInitiationFailedEvent;
import choreograpyhsaga.stock.service.ReserveStockService;
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

import static choreographysaga.common.event.EventTypes.ORDER_STATUS_UPDATE_FAILED_EVENT;
import static choreographysaga.common.event.EventTypes.PAYMENT_INITIATION_FAILED_EVENT;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderListener {
    private final ObjectMapper objectMapper;
    private final ReserveStockService reserveStockService;

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
    @KafkaListener(topics = "Order.events", groupId = "stock-service")
    public void onOrderEvent(@Payload String payload,
                             @Header("eventType") String eventType,
                             @Header("id") String eventId) throws Exception {
        switch (eventType) {
            case ORDER_STATUS_UPDATE_FAILED_EVENT -> {
                log.info("OrderStatusUpdateFailedEvent received: {}", payload);
                OrderStatusUpdateFailedEvent event = objectMapper.readValue(payload, OrderStatusUpdateFailedEvent.class);
                reserveStockService.cancelReservation(event.orderId(), UUID.fromString(eventId), ORDER_STATUS_UPDATE_FAILED_EVENT);
            }
            case PAYMENT_INITIATION_FAILED_EVENT -> {
                log.info("PaymentInitiationFailedEvent received: {}", payload);
                PaymentInitiationFailedEvent event = objectMapper.readValue(payload, PaymentInitiationFailedEvent.class);
                reserveStockService.cancelReservation(event.orderId(), UUID.fromString(eventId), PAYMENT_INITIATION_FAILED_EVENT);
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
