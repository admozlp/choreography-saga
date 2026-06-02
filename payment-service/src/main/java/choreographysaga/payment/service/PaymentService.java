package choreographysaga.payment.service;

import choreographysaga.common.dto.BankTransactionResponse;
import choreographysaga.common.dto.CreatePaymentRequest;
import choreographysaga.common.dto.PaymentCallbackRequest;
import choreographysaga.payment.client.BankServiceManager;
import choreographysaga.payment.converter.PaymentConverter;
import choreographysaga.payment.model.Payment;
import choreographysaga.payment.model.enm.PaymentStatus;
import choreographysaga.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

import static choreographysaga.payment.util.Constant.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository repository;
    private final BankServiceManager bankServiceManager;
    private final PaymentStateManager paymentStateManager;
    private final OutboxEventPublisher outboxEventPublisher;

    public String createPayment(CreatePaymentRequest request) {
        log.info("Creating payment for orderId: {} with amount: {}", request.orderId(), request.amount());
        Payment payment = PaymentConverter.toEntity(request);
        repository.save(payment);
        log.info("Payment created with ID: {}", payment.getId());

        BankTransactionResponse bankTransactionResponse = bankServiceManager.startPayment(payment.getId(), payment.getAmount());
        payment.setStatus(PaymentStatus.STARTED);
        repository.save(payment);
        log.info("Payment started for payment ID: {}", payment.getId());
        return bankTransactionResponse.html();
    }


    public ModelAndView callback(PaymentCallbackRequest request) {
        Optional<Payment> optionalPayment = repository.findById(request.paymentId());
        if (optionalPayment.isEmpty()) {
            log.error("Payment not found for paymentId: {}", request.paymentId());
            return new ModelAndView(ERROR, REDIRECT_URL, ERROR_URL).addObject(MESSAGE, "Payment not found");
        }
        Payment payment = optionalPayment.get();

        if (payment.getStatus() != PaymentStatus.STARTED) {
            log.warn("Payment with ID: {} is not in STARTED status. Current status: {}", payment.getId(), payment.getStatus());
            return new ModelAndView(ERROR, REDIRECT_URL, ERROR_URL).addObject(MESSAGE, "Payment already processed");
        }

        boolean handshake = bankServiceManager.handshake(request.paymentId());
        if (!handshake) {
            log.error("Handshake failed for paymentId: {}. Bank transaction status: {}", request.paymentId(), request.status());
            return paymentStateManager.markAsFailed(request.paymentId());
        }

        return paymentStateManager.markAsCompleted(request.paymentId());
    }
}
