package choreographysaga.order.listener;

import choreographysaga.common.event.OrderStatusUpdateFailedEvent;
import choreographysaga.common.event.PaymentInitiationFailedEvent;
import choreographysaga.order.model.Outbox;
import choreographysaga.order.publisher.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.databind.ObjectMapper;

import static choreographysaga.common.event.EventTypes.ORDER_STATUS_UPDATE_FAILED_EVENT;
import static choreographysaga.common.event.EventTypes.PAYMENT_INITIATION_FAILED_EVENT;

@Component
@RequiredArgsConstructor
public class ApplicationTransactionalEventListener {
    private final OutboxEventPublisher outboxEventPublisher;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void orderStatusUpdateFailed(OrderStatusUpdateFailedEvent orderStatusUpdateFailedEvent) {
        outboxEventPublisher.publish(new Outbox(orderStatusUpdateFailedEvent.orderId().toString(), "Order", ORDER_STATUS_UPDATE_FAILED_EVENT, objectMapper.writeValueAsString(orderStatusUpdateFailedEvent)));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void paymentInitiationFailed(PaymentInitiationFailedEvent paymentInitiationFailedEvent) {
        outboxEventPublisher.publish(new Outbox(paymentInitiationFailedEvent.orderId().toString(), "Order", PAYMENT_INITIATION_FAILED_EVENT, objectMapper.writeValueAsString(paymentInitiationFailedEvent)));
    }
}
