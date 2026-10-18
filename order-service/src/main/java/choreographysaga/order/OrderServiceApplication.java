package choreographysaga.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import security.FeignServiceTokenConfig;
import security.ResourceServerSecurityConfig;


@EnableScheduling
@EnableFeignClients
@SpringBootApplication
@Import({ResourceServerSecurityConfig.class, FeignServiceTokenConfig.class})
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
