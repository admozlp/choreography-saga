package choreographysaga.payment.client;

import choreographysaga.common.dto.BankHandshakeRequest;
import choreographysaga.common.dto.BankTransactionResponse;
import choreographysaga.common.dto.CreateBankTransactionRequest;
import choreographysaga.common.exception.OperationException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Slf4j
@Service
@RequiredArgsConstructor
public class BankServiceManager {
    private final BankClient bankClient;

    @CircuitBreaker(name = "bankService", fallbackMethod = "startPaymentFallback")
    @Retry(name = "bankService")
    public BankTransactionResponse startPayment(Long paymentId, BigDecimal amount) {
        return bankClient.startPayment(new CreateBankTransactionRequest(paymentId, amount, "http://localhost:3530/payments/callback")).getData();
    }

    public BankTransactionResponse startPaymentFallback(Long paymentId, BigDecimal amount, RuntimeException e) {
        FeignException feignException = (FeignException) e;
        log.error("startPaymentFallback triggered. paymentId: {}, Cause: {}", paymentId, feignException.getMessage());
        throw new OperationException(feignException.getMessage(), HttpStatus.valueOf(feignException.status()));
    }


    @CircuitBreaker(name = "bankService", fallbackMethod = "handshakeFallback")
    @Retry(name = "bankService")
    public boolean handshake(Long paymentId) {
        return bankClient.handshake(new BankHandshakeRequest(paymentId)).getData();
    }

    public boolean handshakeFallback(Long paymentId, RuntimeException e) {
        FeignException feignException = (FeignException) e;
        log.error("handshakeFallback triggered. paymentId: {}, Cause: {}", paymentId, feignException.getMessage());
        return false;
    }
}
