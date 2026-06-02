package choreographysaga.payment.client;

import choreographysaga.common.dto.ApiResponse;
import choreographysaga.common.dto.BankHandshakeRequest;
import choreographysaga.common.dto.BankTransactionResponse;
import choreographysaga.common.dto.CreateBankTransactionRequest;
import choreographysaga.payment.client.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;


@FeignClient(
        name = "bank-service",
        url = "http://localhost:2530/bank-transactions",
        configuration = FeignClientConfig.class
)
public interface BankClient {
    @PostMapping
    ApiResponse<BankTransactionResponse> startPayment(CreateBankTransactionRequest request);

    @PostMapping("/handshake")
    ApiResponse<Boolean> handshake(BankHandshakeRequest bankHandshakeRequest);
}
