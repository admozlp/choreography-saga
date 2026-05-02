package choreographysaga.order.service;

import choreographysaga.common.dto.CreatePaymentRequest;
import choreographysaga.common.exception.OperationException;
import choreographysaga.order.client.payment.PaymentClientManager;
import choreographysaga.order.client.stock.ReserveStockOrchestrator;
import choreographysaga.order.converter.OrderConverter;
import choreographysaga.order.dto.CreateOrderRequest;
import choreographysaga.order.model.Order;
import choreographysaga.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final PaymentClientManager paymentClientManager;
    private final ReserveStockOrchestrator reserveStockOrchestrator;


    public String createOrder(CreateOrderRequest request) {
        log.info("Creating order with productId: {} and quantity: {}", request.productId(), request.quantity());
        Order order = OrderConverter.toEntity(request);
        repository.save(order);
        reserveStockOrchestrator.reserveStock(order);

        String html = createPayment(order);
        log.info("Order created with ID: {}", order.getId());
        return html;
    }


    private String createPayment(Order order) {
        try {
            return paymentClientManager.createPayment(new CreatePaymentRequest(order.getId(), BigDecimal.valueOf(order.getQuantity() * 124L)));
        } catch (RuntimeException e) {
            log.error("Payment service failed, updating order status: {}, httpStatusCode: {}", order.getId(), 500);
            throw new OperationException("Ödeme işlemlerinde hata oluştu, lütfen tekrar deneyiniz.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
