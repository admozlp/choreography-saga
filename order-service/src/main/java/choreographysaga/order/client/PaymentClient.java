package choreographysaga.order.client;


import choreographysaga.common.dto.ApiResponse;
import choreographysaga.common.dto.CreatePaymentRequest;
import choreographysaga.order.client.config.ClientConfig;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service",
        url = "http://localhost:3530/payments",
        configuration = ClientConfig.class)
public interface PaymentClient {

    @PostMapping
    ApiResponse<String> createPayment(@RequestBody @Valid CreatePaymentRequest request);
}
