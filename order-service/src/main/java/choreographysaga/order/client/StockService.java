package choreographysaga.order.client;

import choreographysaga.common.dto.DecreaseStockRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {
    private final StockClient stockClient;

    @CircuitBreaker(name = "client", fallbackMethod = "decreaseStockFallback")
    @Retry(name = "client")
    public void decreaseStock(DecreaseStockRequest request, Long orderId) {
        stockClient.decreaseStock(request);
    }

    public void decreaseStockFallback(DecreaseStockRequest request, Long orderId, RuntimeException e) {
        log.error("Stock service fallback triggered. orderId: {} Cause: {}", orderId, e.getMessage());
        throw e;
    }
}
