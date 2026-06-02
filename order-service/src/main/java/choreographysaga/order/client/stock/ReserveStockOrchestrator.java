package choreographysaga.order.client.stock;

import choreographysaga.common.dto.ReserveStockRequest;
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
        order.setStatus(Order.OrderStatus.STOCK_RESERVED);
        applicationEventPublisher.publishEvent(new choreographysaga.common.event.StockReservationCompensationEvent(order.getId()));
        repository.save(order);
    }
}
