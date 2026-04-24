package choreographysaga.order.service;

import choreographysaga.common.dto.CreatePaymentRequest;
import choreographysaga.common.dto.ReserveStockRequest;
import choreographysaga.common.exception.OperationException;
import choreographysaga.order.client.PaymentService;
import choreographysaga.order.client.ReserveStockClient;
import choreographysaga.order.client.StockService;
import choreographysaga.order.converter.OrderConverter;
import choreographysaga.order.dto.CreateOrderRequest;
import choreographysaga.order.model.Order;
import choreographysaga.order.repository.OrderRepository;
import feign.FeignException;
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
    private final StockService stockService;
    private final PaymentService paymentService;
    private final ReserveStockClient reserveStockClient;

    public String createOrder(CreateOrderRequest request) {
        log.info("Creating order with productId: {} and quantity: {}", request.productId(), request.quantity());
        Order order = OrderConverter.toEntity(request);
        repository.save(order);
        reserveStock(order);

        String html = createPayment(order);
        log.info("Order created with ID: {}", order.getId());
        return html;
    }

    private void reserveStock(Order order) {
        reserveStockClient.reserveStock(new ReserveStockRequest(order.getProductId(), order.getQuantity(), order.getId()));
        order.setStatus(Order.OrderStatus.WAITING_FOR_PAYMENT);
        repository.save(order);
    }

    private String createPayment(Order order) {
        try {
            return paymentService.createPayment(new CreatePaymentRequest(order.getId(), BigDecimal.valueOf(order.getQuantity() * 124L)));
        } catch (RuntimeException e) {
//            failOrder(order, Order.OrderStatus.PAYMENT_FAILED);
            HttpStatus status = getHttpStatus(e);
            log.error("Payment service failed, updating order status: {}, httpStatusCode: {}", order.getId(), status.value());
            throw new OperationException("Ödeme işlemlerinde hata oluştu, lütfen tekrar deneyiniz.", status);
        }
    }

    private void failOrder(Order order, Order.OrderStatus status) {
        order.setStatus(status);
        repository.save(order);
    }

    public static HttpStatus getHttpStatus(Exception e) {
        return (e instanceof FeignException fe && fe.status() > 0) ? HttpStatus.valueOf(fe.status()) : HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
