package choreographysaga.order.client.stock;

import choreographysaga.common.dto.ReserveStockRequest;
import choreographysaga.common.exception.OperationException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReserveStockClientManager {
    private final ReserveStockClient reserveStockClient;

    @CircuitBreaker(name = "client", fallbackMethod = "reserveStockFallback")
    @Retry(name = "client")
    public void reserveStock(ReserveStockRequest reserveStockRequest) {
        reserveStockClient.reserveStock(reserveStockRequest);
    }

    public void reserveStockFallback(ReserveStockRequest reserveStockRequest, RuntimeException e) {
        FeignException feignException = (FeignException) e;
        log.error("reserveStockFallback triggered. reserveStockRequest: {}, exceptionMessage: {}", reserveStockRequest.toString(), feignException.getMessage());
        throw new OperationException(feignException.getMessage(), HttpStatus.valueOf(feignException.status()));
    }

}
