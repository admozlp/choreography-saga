package choreographysaga.order.service;

import choreographysaga.common.dto.CreatePaymentRequest;
import choreographysaga.common.dto.ReserveStockRequest;
import choreographysaga.order.client.payment.CreatePaymentOrchestrator;
import choreographysaga.order.client.payment.PaymentClientManager;
import choreographysaga.order.client.stock.ReserveStockClientManager;
import choreographysaga.order.client.stock.ReserveStockOrchestrator;
import choreographysaga.order.converter.OrderConverter;
import choreographysaga.order.dto.CreateOrderRequest;
import choreographysaga.order.model.Order;
import choreographysaga.order.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final ReserveStockOrchestrator reserveStockOrchestrator;
    private final CreatePaymentOrchestrator createPaymentOrchestrator;
    private final ProcessedEventService processedEventService;
    private final ReserveStockClientManager reserveStockClientManager;
    private final PaymentClientManager paymentClientManager;


    public String createOrder(CreateOrderRequest request) {
        log.info("Creating order with productId: {} and quantity: {}", request.productId(), request.quantity());
        Order order = OrderConverter.toEntity(request);
        repository.save(order);
        reserveStockClientManager.reserveStock(new ReserveStockRequest(order.getProductId(), order.getQuantity(), order.getId()));
        reserveStockOrchestrator.updateStatus(order.getId());
        log.info("Stock reserved for order ID: {}", order.getId());

        String html = paymentClientManager.createPayment(new CreatePaymentRequest(order.getId(), BigDecimal.valueOf(order.getQuantity()).multiply(BigDecimal.valueOf(124L))));
        createPaymentOrchestrator.updateStatus(order.getId());
        log.info("Payment created for order ID: {}", order.getId());
        return html;
    }


    @Transactional
    public void markAsPaymentFailed(Long orderId, UUID eventId, String eventType) {
        if (processedEventService.isProcessed(eventId)) {
            log.info("Event already processed in markAsPaymentFailed, eventId: {}", eventId);
            return;
        }

        repository.findByIdAndStatus(orderId, Order.OrderStatus.PAYMENT_CREATED).ifPresentOrElse(
                order -> {
                    order.setStatus(Order.OrderStatus.PAYMENT_FAILED);
                    log.info("Order marked as payment failed, orderId: {}", orderId);
                },
                () -> {
                    log.error("Order not found in markAsPaymentFailed with id: {}", orderId);
                    throw new EntityNotFoundException("Order not found with id: " + orderId);
                }
        );
        processedEventService.markAsProcessed(eventId, eventType, String.valueOf(orderId));
    }


    @Transactional
    public void markAsPaymentCompleted(Long orderId, UUID eventId, String eventType) {
        if (processedEventService.isProcessed(eventId)) {
            log.info("Event already processed in markAsPaymentCompleted, eventId: {}", eventId);
            return;
        }

        log.info("tx active? {}",
                org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive());

        repository.findByIdAndStatus(orderId, Order.OrderStatus.PAYMENT_CREATED).ifPresentOrElse(
                order -> {
                    order.setStatus(Order.OrderStatus.PAYMENT_COMPLETED);
                    log.info("Order marked as payment completed, orderId: {}", orderId);
                },
                () -> {
                    log.error("Order not found in markAsPaymentCompleted with id: {}", orderId);
                    throw new EntityNotFoundException("Order not found with id: " + orderId);
                }
        );
        processedEventService.markAsProcessed(eventId, eventType, String.valueOf(orderId));
    }
}
