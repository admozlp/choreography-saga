package choreographysaga.order.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {
    private final ReserveStockClient reserveStockClient;

//    @CircuitBreaker(name = "client", fallbackMethod = "reserveStockFallback")
//    @Retry(name = "client")
//    public void reserveStock(ReserveStockRequest request, Long orderId) {
//        stockClient.reserveStock(request);
//    }
//
//    public void reserveStockFallback(ReserveStockRequest request, Long orderId, RuntimeException e) {
//        log.error("Stock service fallback triggered. orderId: {} Cause: {}", orderId, e.getMessage());
//        HttpStatus status = getHttpStatus(e);
//        throw new OperationException("Stok işlemlerinde hata oluştu, lütfen tekrar deneyiniz.", status);
//    }
}
