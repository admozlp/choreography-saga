package choreographysaga.order.client.payment;


import choreographysaga.common.event.PaymentInitiationFailedEvent;
import choreographysaga.order.model.Order;
import choreographysaga.order.model.Outbox;
import choreographysaga.order.publisher.OutboxEventPublisher;
import choreographysaga.order.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static choreographysaga.common.event.EventTypes.PAYMENT_INITIATION_FAILED_EVENT;
import static choreographysaga.order.model.Order.OrderStatus.STOCK_RESERVED;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreatePaymentOrchestrator {
    private final OrderRepository repository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final OutboxEventPublisher outboxEventPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    public void updateStatus(Long orderId) {
        repository.findByIdAndStatus(orderId, STOCK_RESERVED)
                .ifPresentOrElse(
                        order -> {
                            applicationEventPublisher.publishEvent(new PaymentInitiationFailedEvent(orderId));
                            order.setStatus(Order.OrderStatus.PAYMENT_CREATED);
                        },
                        () -> {
                            outboxEventPublisher.publish(new Outbox(orderId.toString(), "Order", PAYMENT_INITIATION_FAILED_EVENT, objectMapper.writeValueAsString(new PaymentInitiationFailedEvent(orderId))));
                            throw new EntityNotFoundException("Order not found with id: " + orderId);
                        }
                );
    }
}
