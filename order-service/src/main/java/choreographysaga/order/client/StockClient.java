package choreographysaga.order.client;

import choreographysaga.common.dto.DecreaseStockRequest;
import choreographysaga.order.client.config.StockClientConfig;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "stock-service",
        url = "http://localhost:3532/stocks",
        configuration = StockClientConfig.class,
        fallbackFactory = StockClientFallbackFactory.class
)
public interface StockClient {

    @PutMapping
    void decreaseStock(@RequestBody @Valid DecreaseStockRequest request);

}
