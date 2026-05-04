package choreograpyhsaga.stock.listener;

import choreograpyhsaga.stock.exception.EventConsumeException;
import choreograpyhsaga.stock.listener.event.StockReservationCompensationEvent;
import choreograpyhsaga.stock.service.ReserveStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static choreographysaga.common.event.EventTypes.STOCK_RESERVATION_COMPENSATION_EVENT;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderListener {
    private final ObjectMapper objectMapper;
    private final ReserveStockService reserveStockService;

    @KafkaListener(topics = "Order.events", groupId = "stock-service")
    public void onOrderEvent(@Payload String payload, @Header("eventType") String eventType, @Header("id") String eventId) {
        switch (eventType) {
            case STOCK_RESERVATION_COMPENSATION_EVENT -> {
                try {
                    log.info("StockReservationCompensationEvent received: {}", payload);
                    StockReservationCompensationEvent event = objectMapper.readValue(payload, StockReservationCompensationEvent.class);
                    reserveStockService.cancelReservation(event.orderId(), UUID.fromString(eventId));
                } catch (Exception e) {
                    log.error("Failed to process StockReservationCompensationEvent, eventId: {}, error: {}", eventId, e.getMessage());
                    throw new EventConsumeException("Failed to process StockReservationCompensationEvent, eventId: " + eventId, e);
                }
            }
            case null -> log.warn("Received event with null eventType, eventId: {}, ignoring", eventId);
            default -> log.debug("Ignoring eventType={}", eventType);
        }
    }
}
