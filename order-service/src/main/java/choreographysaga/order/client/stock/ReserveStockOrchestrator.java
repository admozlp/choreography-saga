package choreographysaga.order.client.stock;

import choreographysaga.common.event.OrderStatusUpdateFailedEvent;
import choreographysaga.order.model.Order;
import choreographysaga.order.model.Outbox;
import choreographysaga.order.publisher.OutboxEventPublisher;
import choreographysaga.order.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static choreographysaga.common.event.EventTypes.ORDER_STATUS_UPDATE_FAILED_EVENT;
import static choreographysaga.order.model.Order.OrderStatus.STOCK_WILL_BE_RESERVED;

@Service
@RequiredArgsConstructor
public class ReserveStockOrchestrator {
    private final OrderRepository repository;
    private final OutboxEventPublisher outboxEventPublisher;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void updateStatus(Long orderId) {
        repository.findByIdAndStatus(orderId, STOCK_WILL_BE_RESERVED)
                .ifPresentOrElse(
                        order -> {
                            applicationEventPublisher.publishEvent(new OrderStatusUpdateFailedEvent(orderId));
                            order.setStatus(Order.OrderStatus.STOCK_RESERVED);
                        },
                        () -> {
                            outboxEventPublisher.publish(new Outbox(orderId.toString(), "Order", ORDER_STATUS_UPDATE_FAILED_EVENT, objectMapper.writeValueAsString(new OrderStatusUpdateFailedEvent(orderId))));
                            throw new EntityNotFoundException("Order not found with id: " + orderId);
                        }
                );
    }
}
