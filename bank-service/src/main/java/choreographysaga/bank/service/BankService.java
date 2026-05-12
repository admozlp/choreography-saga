package choreographysaga.bank.service;

import choreographysaga.common.dto.BankResponse;
import choreographysaga.common.dto.StartPaymentRequest;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Service
public class BankService {
    @Value("classpath:templates/start-payment.html")
    private Resource htmlTemplate;
    private String cachedTemplate;

    @PostConstruct
    private void loadTemplate() throws IOException {
        cachedTemplate = htmlTemplate.getContentAsString(StandardCharsets.UTF_8);
    }

    public BankResponse startPayment(StartPaymentRequest request) {
        log.info("Bank process started for paymentId: {} with amount: {}", request.paymentId(), request.amount());
        // generate optCode and save to database

        String html = generateHtml(request);
        BankResponse bankResponse = new BankResponse(html, request.paymentId().toString(), UUID.randomUUID());
        log.info("Bank process completed for paymentId: {}. Returning HTML response.", request.paymentId());
        return bankResponse;
    }

    private String generateHtml(StartPaymentRequest request) {
        return cachedTemplate
                .replace("${MERCHANT_NAME}", "Adem Özalp A.Ş")
                .replace("${CURRENCY_SYMBOL}", "₺")
                .replace("${AMOUNT}", request.amount().toPlainString())
                .replace("${MASKED_PHONE}", "+90 5** *** ** 47")
                .replace("${PAYMENT_ID}", request.paymentId().toString());
    }

    public void confirmPayment(String paymentId, String optCode) {
        log.info("Confirming payment with paymentId: {}", paymentId);
        //check otpCode and paymentId, if valid mark payment as completed in database
        // if invalid, log error and throw exception
        // redirect to merchant callback url with payment status
    }

    public void handshake(String paymentId, String status) {
        log.info("Handshake received for paymentId: {}", paymentId);
        // This method can be used to verify the connection between bank and payment service
    }
}
