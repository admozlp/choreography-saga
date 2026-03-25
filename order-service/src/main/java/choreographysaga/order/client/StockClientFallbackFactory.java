package choreographysaga.order.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StockClientFallbackFactory implements FallbackFactory<StockClient> {

    @Override
    public StockClient create(Throwable cause) {
        return request -> {
            log.error("Stock service fallback triggered. Cause: {}", cause.getMessage());
            throw (RuntimeException) cause;
        };
    }
}
