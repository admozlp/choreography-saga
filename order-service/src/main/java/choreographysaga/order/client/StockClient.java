package choreographysaga.order.client;


import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "stock-service", url = "http://localhost:3532/stocks")
public interface StockClient {

    @PutMapping
    void decreaseStock(@RequestBody @Valid choreographysaga.common.dto.DecreaseStockRequest request);


}
