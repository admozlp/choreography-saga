package choreographysaga.order.service;

import choreographysaga.common.dto.DecreaseStockRequest;
import choreographysaga.common.exception.OperationException;
import choreographysaga.order.client.StockService;
import choreographysaga.order.converter.OrderConverter;
import choreographysaga.order.dto.CreateOrderRequest;
import choreographysaga.order.model.Order;
import choreographysaga.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final StockService stockService;

    public String createOrder(CreateOrderRequest request) {
        log.info("Creating order with productId: {} and quantity: {}", request.productId(), request.quantity());
        Order order = repository.save(OrderConverter.toEntity(request));

        try {
            stockService.decreaseStock(new DecreaseStockRequest(request.productId(), request.quantity()), order.getId());
        } catch (Exception e) {
            log.error("Stock service failed, updating order status: {}", order.getId());
            order.setStatus(Order.OrderStatus.STOCK_INSUFFICIENT);
            repository.save(order);
            throw new OperationException("Hata oluştu, lütfen tekrar deneyiniz.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // TODO: payment a gidip html alacak
        try {
            // paymentService.createPayment(...);
        } catch (Exception e) {
            log.error("Payment service failed, updating order status: {}", order.getId());
            order.setStatus(Order.OrderStatus.PAYMENT_FAILED);
            repository.save(order);
            throw new OperationException("Hata oluştu, lütfen tekrar deneyiniz.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.info("Order created with ID: {}", order.getId());
        return "Order created with ID: " + order.getId();
    }
}
