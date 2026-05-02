package choreographysaga.order.client.payment;


import choreographysaga.common.dto.CreatePaymentRequest;
import choreographysaga.common.exception.OperationException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentClientManager {
    private final PaymentClient paymentClient;

    @CircuitBreaker(name = "client", fallbackMethod = "createPaymentFallback")
    @Retry(name = "client")
    public String createPayment(CreatePaymentRequest request) {
        return paymentClient.createPayment(request).getData();
    }

    public String createPaymentFallback(CreatePaymentRequest request, RuntimeException e) {
        FeignException feignException = (FeignException) e;
        log.error("createPaymentFallback triggered. orderId: {} Cause: {}, exceptionMessage: {}", request.orderId(), e.getMessage(), feignException.getMessage());
        throw new OperationException(e.getMessage(), HttpStatus.valueOf(feignException.status()));
    }
}
