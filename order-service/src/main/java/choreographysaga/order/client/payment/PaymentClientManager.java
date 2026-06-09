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

    public String createPaymentFallback(CreatePaymentRequest request, Throwable e) {
        log.error("createPaymentFallback triggered. orderId: {}, error: {}", request.orderId(), e.toString());

        // Only a real HTTP response carries a usable status; FeignException.status() is -1
        // for connect/read failures (timeout, connection refused).
        if (e instanceof FeignException fe && fe.status() > 0) {
            throw new OperationException(fe.getMessage(), HttpStatus.valueOf(fe.status()));
        }

        // Circuit open (CallNotPermittedException), no-response Feign failures (status -1),
        // or anything else: payment service is effectively unavailable.
        throw new OperationException("Payment service unavailable: " + e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }
}
