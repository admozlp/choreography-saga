package choreographysaga.bank.service;

import choreographysaga.bank.converter.BankTransactionConverter;
import choreographysaga.bank.dto.ConfirmPaymentRequest;
import choreographysaga.bank.model.BankTransaction;
import choreographysaga.bank.model.enm.BankTransactionStatus;
import choreographysaga.bank.repository.BankTransactionRepository;
import choreographysaga.common.dto.BankResponse;
import choreographysaga.common.dto.StartPaymentRequest;
import choreographysaga.common.exception.OperationException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankTransactionService {
    @Value("classpath:templates/start-payment.html")
    private Resource htmlTemplate;
    private String cachedTemplate;

    @PostConstruct
    private void loadTemplate() throws IOException {
        cachedTemplate = htmlTemplate.getContentAsString(StandardCharsets.UTF_8);
    }

    private final BankTransactionRepository repository;

    public BankResponse startPayment(StartPaymentRequest request) {
        log.info("Bank process started for paymentId: {} with amount: {}", request.paymentId(), request.amount());
        Integer otpCode = generateOtpCode();
        repository.save(BankTransactionConverter.toEntity(request, otpCode));

        String html = generateHtml(request);
        BankResponse bankResponse = new BankResponse(html, request.paymentId().toString(), UUID.randomUUID());
        log.info("Bank process completed for paymentId: {}. Returning HTML response.", request.paymentId());
        return bankResponse;
    }

    private Integer generateOtpCode() {
        int i = 0;
        do {
            int otpCode = ThreadLocalRandom.current().nextInt(900_000) + 100_000;
            if (!repository.existsByOtpCode(otpCode))
                return otpCode;
            i++;
        } while (i < 10);
        throw new OperationException("Couldn't generate otpCode", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String generateHtml(StartPaymentRequest request) {
        return cachedTemplate
                .replace("${MERCHANT_NAME}", "Adem Özalp A.Ş")
                .replace("${CURRENCY_SYMBOL}", "₺")
                .replace("${AMOUNT}", request.amount().toPlainString())
                .replace("${MASKED_PHONE}", "+90 5** *** ** 47")
                .replace("${PAYMENT_ID}", request.paymentId().toString());
    }

    public void confirmPayment(ConfirmPaymentRequest request) {
        log.info("Confirming payment with paymentId: {}", request.paymentId());
        BankTransaction bankTransaction = repository.findByPaymentIdAndOtpCodeAndStatus(request.paymentId(), BankTransactionStatus.PENDING)
                .orElseThrow(() -> new OperationException("Transaction not found", HttpStatus.NOT_FOUND));

        if (bankTransaction.getExpiresAt().isBefore(java.time.Instant.now())) {
            bankTransaction.setStatus(BankTransactionStatus.EXPIRED);
            repository.save(bankTransaction);
            log.error("Payment expired for paymentId: {}", request.paymentId());
            throw new OperationException("Payment has expired", HttpStatus.BAD_REQUEST);
        }

        if (bankTransaction.getOtpAttemptCount() >= 3) {
            bankTransaction.setStatus(BankTransactionStatus.FAILED);
            repository.save(bankTransaction);
            log.error("Payment failed due to too many invalid attempts for paymentId: {}", request.paymentId());
            throw new OperationException("Too many invalid attempts. Payment failed.", HttpStatus.BAD_REQUEST);
        }

        if (!bankTransaction.getOtpCode().equals(request.otpCode())) {
            log.error("Invalid OTP code for paymentId: {}", request.paymentId());
            bankTransaction.setOtpAttemptCount((short) (bankTransaction.getOtpAttemptCount() + 1));
            repository.save(bankTransaction);
            throw new OperationException("Invalid OTP code", HttpStatus.BAD_REQUEST);
        }

        bankTransaction.setStatus(BankTransactionStatus.CONFIRMED);
        repository.save(bankTransaction);

        log.info("Payment confirmed for paymentId: {}", request.paymentId());
    }

    public void handshake(String paymentId, String status) {
        log.info("Handshake received for paymentId: {}", paymentId);
        // This method can be used to verify the connection between bank and payment service
    }
}
