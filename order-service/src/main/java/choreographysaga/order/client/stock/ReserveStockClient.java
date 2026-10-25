package choreographysaga.order.client.stock;

import choreographysaga.common.dto.ReserveStockRequest;
import choreographysaga.order.client.config.FeignClientConfig;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "stock-service",
        url = "${services.stock.url}",
        path = "/reserve-stocks",
        configuration = FeignClientConfig.class
)
public interface ReserveStockClient {

    @PostMapping("/internal/reserve")
    void reserveStock(@RequestBody @Valid ReserveStockRequest request);
}
