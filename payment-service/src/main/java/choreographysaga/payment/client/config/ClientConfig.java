package choreographysaga.payment.client.config;

import choreographysaga.payment.client.errordecoder.ClientErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class ClientConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new ClientErrorDecoder();
    }
}
