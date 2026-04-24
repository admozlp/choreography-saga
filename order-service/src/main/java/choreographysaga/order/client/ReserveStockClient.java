package choreographysaga.order.client;

import choreographysaga.common.dto.ReserveStockRequest;
import choreographysaga.common.exception.OperationException;
import choreographysaga.order.client.config.ClientConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static choreographysaga.order.service.OrderService.getHttpStatus;

@FeignClient(
        name = "stock-service",
        url = "http://localhost:3532/reserve-stocks",
        configuration = ClientConfig.class
)
public interface ReserveStockClient {

    @CircuitBreaker(name = "client", fallbackMethod = "reserveStockFallback")
    @Retry(name = "client")
    @PostMapping("/reserve")
    void reserveStock(@RequestBody @Valid ReserveStockRequest request);

    default void reserveStockFallback(ReserveStockRequest request, RuntimeException e) {
        HttpStatus status = getHttpStatus(e);
        throw new OperationException("Stok işlemlerinde hata oluştu, lütfen tekrar deneyiniz.", status);
    }

}
