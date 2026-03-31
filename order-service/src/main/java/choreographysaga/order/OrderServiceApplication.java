package choreographysaga.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients
@SpringBootApplication
public class OrderServiceApplication {
    // TODO
    // frontend html i alacak
    // frontendden payment servisteki callback api alacak.
    // payment servis eventları başlatacak
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
