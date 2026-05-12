package choreographysaga.payment.service;

import choreographysaga.common.dto.BankResponse;
import choreographysaga.common.dto.CreatePaymentRequest;
import choreographysaga.payment.client.BankServiceManager;
import choreographysaga.payment.converter.PaymentConverter;
import choreographysaga.payment.model.Payment;
import choreographysaga.payment.model.enm.PaymentStatus;
import choreographysaga.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository repository;
    private final BankServiceManager bankServiceManager;

    public String createPayment(CreatePaymentRequest request) {
        log.info("Creating payment for orderId: {} with amount: {}", request.orderId(), request.amount());
        Payment payment = PaymentConverter.toEntity(request);
        repository.save(payment);
        log.info("Payment created with ID: {}", payment.getId());

        BankResponse bankResponse = bankServiceManager.startPayment(payment.getId(), payment.getAmount());
        payment.setStatus(PaymentStatus.STARTED);
        repository.save(payment);
        log.info("Payment started for payment ID: {}", payment.getId());
        return bankResponse.html();
    }


    public void callback(String paymentId, String status) {
        // This method will be called by the bank service after payment is processed
    }
}
