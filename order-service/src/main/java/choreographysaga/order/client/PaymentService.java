package choreographysaga.order.client;


import choreographysaga.common.dto.CreatePaymentRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentClient paymentClient;

    @CircuitBreaker(name = "client", fallbackMethod = "createPaymentFallback")
    @Retry(name = "client")
    public String createPayment(CreatePaymentRequest request) {
        return paymentClient.createPayment(request).getData();
    }

    public String createPaymentFallback(CreatePaymentRequest request, RuntimeException e) {
        log.error("Payment service fallback triggered. orderId: {} Cause: {}", request.orderId(), e.getMessage());
        throw e;
    }


}
