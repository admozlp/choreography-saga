package choreographysaga.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients
@SpringBootApplication
public class OrderServiceApplication {
    // TODO
    // order service
    // stock service
    // payment service
    // bank service
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
