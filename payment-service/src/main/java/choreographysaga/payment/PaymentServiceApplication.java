package choreographysaga.payment;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import security.FeignServiceTokenConfig;
import security.ResourceServerSecurityConfig;

@SpringBootApplication
@EnableFeignClients
@Import({ResourceServerSecurityConfig.class, FeignServiceTokenConfig.class})
public class PaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}