package choreographysaga.order.client.stock;

import choreographysaga.common.dto.ReserveStockRequest;
import choreographysaga.order.listener.event.StockReservedEvent;
import choreographysaga.order.model.Order;
import choreographysaga.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReserveStockOrchestrator {
    private final ReserveStockClientManager reserveStockClientManager;
    private final OrderRepository repository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void reserveStock(Order order) {
        reserveStockClientManager.reserveStock(new ReserveStockRequest(order.getProductId(), order.getQuantity(), order.getId()));
        order.setStatus(Order.OrderStatus.WAITING_FOR_PAYMENT);
        applicationEventPublisher.publishEvent(new StockReservedEvent(order.getId(), order.getProductId(), order.getQuantity()));
        order.setQuantity(null);
        repository.save(order);
    }
}
