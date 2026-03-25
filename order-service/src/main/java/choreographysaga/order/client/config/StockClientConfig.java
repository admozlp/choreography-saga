package choreographysaga.order.client.config;

import choreographysaga.order.client.errordecoder.StockServiceErrorDecoder;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class StockClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new StockServiceErrorDecoder();
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(1000, 1000, 3);
    }
}
