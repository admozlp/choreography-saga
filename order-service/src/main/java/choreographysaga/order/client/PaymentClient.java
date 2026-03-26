package choreographysaga.order.client;


import choreographysaga.common.dto.ApiResponse;
import choreographysaga.common.dto.CreatePaymentRequest;
import choreographysaga.order.client.config.ClientConfig;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service",
        url = "http://localhost:3530",
        configuration = ClientConfig.class)
public interface PaymentClient {

    @PutMapping
    ApiResponse<String> createPayment(@RequestBody @Valid CreatePaymentRequest request);
}
