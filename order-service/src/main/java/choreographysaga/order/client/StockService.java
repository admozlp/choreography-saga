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

    @CircuitBreaker(name = "stockService", fallbackMethod = "decreaseStockFallback")
    @Retry(name = "stockService")
    public void decreaseStock(DecreaseStockRequest request) {
        stockClient.decreaseStock(request);
    }

    public void decreaseStockFallback(DecreaseStockRequest request, Exception e) {
        log.error("Stock service fallback triggered. Cause: {}", e.getMessage());
        if (e instanceof RuntimeException runtimeException) {
            throw runtimeException;
        }
        throw new RuntimeException(e);
    }
}
