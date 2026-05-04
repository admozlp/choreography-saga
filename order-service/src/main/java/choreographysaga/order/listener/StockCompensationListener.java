package choreographysaga.order.listener;

import choreographysaga.order.listener.event.StockReservedEvent;
import choreographysaga.order.model.Outbox;
import choreographysaga.order.publisher.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.databind.ObjectMapper;

import static choreographysaga.common.event.EventTypes.STOCK_RESERVATION_COMPENSATION_EVENT;

@Component
@RequiredArgsConstructor
public class StockCompensationListener {
    private final OutboxEventPublisher outboxEventPublisher;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void compensateReservedStock(StockReservedEvent stockReservedEvent) {
        outboxEventPublisher.publish(new Outbox(stockReservedEvent.orderId().toString(), "Order", STOCK_RESERVATION_COMPENSATION_EVENT, objectMapper.writeValueAsString(stockReservedEvent)));
    }
}
