package choreographysaga.payment.client;

import choreographysaga.common.dto.ApiResponse;
import choreographysaga.common.dto.BankResponse;
import choreographysaga.common.dto.StartPaymentRequest;
import choreographysaga.payment.client.config.FeignClientConfig;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(
        name = "bank-service",
        url = "http://localhost:2530/bank",
        configuration = FeignClientConfig.class
)
public interface BankClient {
    @PostMapping
    ApiResponse<BankResponse> startPayment(@RequestBody @Valid StartPaymentRequest request);
}
