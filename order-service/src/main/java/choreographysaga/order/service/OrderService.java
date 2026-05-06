package choreographysaga.order.service;

import choreographysaga.order.client.payment.CreatePaymentOrchestrator;
import choreographysaga.order.client.stock.ReserveStockOrchestrator;
import choreographysaga.order.converter.OrderConverter;
import choreographysaga.order.dto.CreateOrderRequest;
import choreographysaga.order.model.Order;
import choreographysaga.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final ReserveStockOrchestrator reserveStockOrchestrator;
    private final CreatePaymentOrchestrator createPaymentOrchestrator;


    public String createOrder(CreateOrderRequest request) {
        log.info("Creating order with productId: {} and quantity: {}", request.productId(), request.quantity());
        Order order = OrderConverter.toEntity(request);
        repository.save(order);
        reserveStockOrchestrator.reserveStock(order);
        log.info("Stock reserved for order ID: {}", order.getId());

        String html = createPaymentOrchestrator.createPayment(order);
        log.info("Payment created for order ID: {}", order.getId());
        return html;
    }
}
