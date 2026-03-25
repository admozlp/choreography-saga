package choreographysaga.order.service;

import choreographysaga.common.dto.DecreaseStockRequest;
import choreographysaga.order.client.StockClient;
import choreographysaga.order.converter.OrderConverter;
import choreographysaga.order.dto.CreateOrderRequest;
import choreographysaga.order.exception.StockServiceException;
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
    private final StockClient stockClient;

    public String createOrder(CreateOrderRequest request) {
        log.info("Creating order with productId: {} and quantity: {}", request.productId(), request.quantity());
        Order order = repository.save(OrderConverter.toEntity(request));

        try {
            stockClient.decreaseStock(new DecreaseStockRequest(request.productId(), request.quantity()));
        } catch (Exception e) {
            log.error("Stock service failed, deleting order: {}", order.getId());
            repository.deleteById(order.getId());
            throw new StockServiceException("Hata oluştu, lütfen tekrar deneyiniz.", e);
        }

        // TODO: payment a gidip html alacak
        log.info("Order created with ID: {}", order.getId());
        return "Order created with ID: " + order.getId();
    }
}
