package choreographysaga.bank.service;

import choreographysaga.bank.converter.BankTransactionConverter;
import choreographysaga.bank.dto.ConfirmPaymentRequest;
import choreographysaga.bank.model.BankTransaction;
import choreographysaga.bank.repository.BankTransactionRepository;
import choreographysaga.common.dto.BankHandshakeRequest;
import choreographysaga.common.dto.BankTransactionResponse;
import choreographysaga.common.dto.CreateBankTransactionRequest;
import choreographysaga.common.exception.OperationException;
import choreographysaga.common.model.enm.BankTransactionStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Value("classpath:templates/payment-callback.html")
    private Resource callbackTemplate;
    private String cachedCallbackTemplate;

    @PostConstruct
    private void loadTemplates() throws IOException {
        cachedTemplate = htmlTemplate.getContentAsString(StandardCharsets.UTF_8);
        cachedCallbackTemplate = callbackTemplate.getContentAsString(StandardCharsets.UTF_8);
    }

    private final BankTransactionRepository repository;

    public BankTransactionResponse startPayment(CreateBankTransactionRequest request) {
        log.info("Bank process started for paymentId: {} with amount: {}", request.paymentId(), request.amount());
        Integer otpCode = generateOtpCode();
        repository.save(BankTransactionConverter.toEntity(request, otpCode));

        String html = generateHtml(request);
        BankTransactionResponse bankTransactionResponse = new BankTransactionResponse(html, request.paymentId().toString(), UUID.randomUUID());
        log.info("Bank process completed for paymentId: {}. Returning HTML response.", request.paymentId());
        return bankTransactionResponse;
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

    private String generateHtml(CreateBankTransactionRequest request) {
        return cachedTemplate
                .replace("${MERCHANT_NAME}", "Adem Özalp A.Ş")
                .replace("${CURRENCY_SYMBOL}", "₺")
                .replace("${AMOUNT}", request.amount().toPlainString())
                .replace("${MASKED_PHONE}", "+90 5** *** ** 47")
                .replace("${PAYMENT_ID}", request.paymentId().toString());
    }


    @Transactional(noRollbackFor = OperationException.class)
    public String confirmPayment(ConfirmPaymentRequest request) {
        log.info("Confirming payment with paymentId: {}", request.paymentId());

        BankTransaction bankTransaction = repository.findByPaymentIdAndStatus(request.paymentId())
                .orElseThrow(() -> new OperationException("Transaction not found", HttpStatus.NOT_FOUND));

        if (bankTransaction.getStatus() != BankTransactionStatus.PENDING) {
            log.error("Transaction already processed, paymentId: {}, transaction status: {}", bankTransaction.getPaymentId(), bankTransaction.getStatus());
            throw new OperationException("Transaction already processed", HttpStatus.BAD_REQUEST);
        }

        if (bankTransaction.getExpiresAt().isBefore(java.time.Instant.now())) {
            bankTransaction.setStatus(BankTransactionStatus.EXPIRED);
            log.error("Payment expired for paymentId: {}", request.paymentId());
            throw new OperationException("Payment has expired", HttpStatus.BAD_REQUEST);
        }

        if (bankTransaction.getOtpAttemptCount() >= 3) {
            bankTransaction.setStatus(BankTransactionStatus.FAILED);
            log.error("Payment failed due to too many invalid attempts for paymentId: {}", request.paymentId());
            throw new OperationException("Too many invalid attempts. Payment failed.", HttpStatus.BAD_REQUEST);
        }

        if (!bankTransaction.getOtpCode().equals(request.otpCode())) {
            log.error("Invalid OTP code for paymentId: {}", request.paymentId());
            bankTransaction.setOtpAttemptCount((short) (bankTransaction.getOtpAttemptCount() + 1));
            throw new OperationException("Invalid OTP code", HttpStatus.BAD_REQUEST);
        }
        bankTransaction.setStatus(BankTransactionStatus.CONFIRMED);

        log.info("Payment confirmed for paymentId: {}", request.paymentId());
        return generateCallbackForm(bankTransaction);
    }

    private String generateCallbackForm(BankTransaction bankTransaction) {
        return cachedCallbackTemplate
                .replace("${CALLBACK_URL}", bankTransaction.getCallbackUrl())
                .replace("${PAYMENT_ID}", bankTransaction.getPaymentId().toString())
                .replace("${STATUS}", bankTransaction.getStatus().name());
    }

    @Transactional
    public boolean handshake(BankHandshakeRequest request) {
        log.info("Handshake received for paymentId: {}", request.paymentId());
        BankTransaction bankTransaction = repository.findByPaymentId(request.paymentId())
                .orElseThrow(() -> new OperationException("Transaction not found for handshake", HttpStatus.NOT_FOUND));

        if (bankTransaction.getStatus() == BankTransactionStatus.COMPLETED) {
            return true;
        }

        // buradaki doğrulama bankanın hata vermesi durumunu simüle ediyor.
        if (bankTransaction.getStatus() != BankTransactionStatus.CONFIRMED) {
            log.error("Handshake failed for paymentId: {}. Transaction status is not CONFIRMED. Current status: {}", request.paymentId(), bankTransaction.getStatus());
            return false;
        }

        // para transferini gerçekleştirmeyi simüle eder
        bankTransaction.setStatus(BankTransactionStatus.COMPLETED);
        log.info("Handshake successful for paymentId: {}. Transaction marked as COMPLETED.", request.paymentId());
        return true;
    }
}
