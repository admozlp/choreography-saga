package choreograpyhsaga.stock.listener;

import choreograpyhsaga.stock.listener.event.StockReservationCompensationEvent;
import choreograpyhsaga.stock.service.ReserveStockService;
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

import static choreographysaga.common.event.EventTypes.STOCK_RESERVATION_COMPENSATION_EVENT;

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
                    IllegalArgumentException.class
            }
    )
    @KafkaListener(topics = "Order.events", groupId = "stock-service")
    public void onOrderEvent(@Payload String payload,
                             @Header("eventType") String eventType,
                             @Header("id") String eventId) throws Exception {
        switch (eventType) {
            case STOCK_RESERVATION_COMPENSATION_EVENT -> {
                log.info("StockReservationCompensationEvent received: {}", payload);
                StockReservationCompensationEvent event = objectMapper.readValue(payload, StockReservationCompensationEvent.class);
                reserveStockService.cancelReservation(event.orderId(), UUID.fromString(eventId));
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
