package choreographysaga.payment.client;

import choreographysaga.common.dto.BankResponse;
import choreographysaga.common.dto.StartPaymentRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Slf4j
@Service
@RequiredArgsConstructor
public class BankService {
    private final BankClient bankClient;

    @CircuitBreaker(name = "bankService", fallbackMethod = "startPaymentFallback")
    @Retry(name = "bankService")
    public BankResponse startPayment(Long paymentId, BigDecimal amount) {
        return bankClient.startPayment(new StartPaymentRequest(paymentId, amount)).getData();
    }

    public BankResponse startPaymentFallback(Long paymentId, BigDecimal amount, RuntimeException e) {
        log.error("Bank service fallback triggered. paymentId: {}, Cause: {}", paymentId, e.getMessage());
        throw e;
    }
}
