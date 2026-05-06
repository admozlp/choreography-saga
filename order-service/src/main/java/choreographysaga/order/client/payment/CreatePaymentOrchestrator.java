package choreographysaga.order.client.payment;


import choreographysaga.common.dto.CreatePaymentRequest;
import choreographysaga.order.listener.event.StockReservedEvent;
import choreographysaga.order.model.Order;
import choreographysaga.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreatePaymentOrchestrator {
    private final OrderRepository repository;
    private final PaymentClientManager paymentClientManager;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public String createPayment(Order order) {
        applicationEventPublisher.publishEvent(new StockReservedEvent(order.getId(), order.getProductId(), order.getQuantity()));
        String html = paymentClientManager.createPayment(new CreatePaymentRequest(order.getId(), BigDecimal.valueOf(order.getQuantity()).multiply(BigDecimal.valueOf(124L))));
        order.setStatus(Order.OrderStatus.PAYMENT_CREATED);
        repository.save(order);
        return html;
    }
}
