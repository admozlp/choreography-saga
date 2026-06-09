package choreographysaga.payment.service;

import choreographysaga.common.dto.BankTransactionResponse;
import choreographysaga.common.dto.CreatePaymentRequest;
import choreographysaga.common.dto.PaymentCallbackRequest;
import choreographysaga.common.exception.OperationException;
import choreographysaga.payment.client.BankServiceManager;
import choreographysaga.payment.converter.PaymentConverter;
import choreographysaga.payment.model.Payment;
import choreographysaga.payment.model.enm.PaymentStatus;
import choreographysaga.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

import static choreographysaga.payment.util.Constant.ERROR_URL;
import static choreographysaga.payment.util.Constant.MESSAGE;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository repository;
    private final BankServiceManager bankServiceManager;
    private final PaymentStateManager paymentStateManager;

    public String createPayment(CreatePaymentRequest request) {
        log.info("Creating payment for orderId: {} with amount: {}", request.orderId(), request.amount());
        Optional<Payment> existing = repository.findByOrderId(request.orderId());
        if (existing.isPresent()) {
            Payment payment = existing.get();
            if (payment.getStatus() == PaymentStatus.STARTED && payment.getHtml() != null) {
                log.info("Payment already STARTED for orderId: {}, replaying stored form (idempotent)", request.orderId());
                return payment.getHtml();
            }
            log.warn("Payment already exists for orderId: {} in status {}, rejecting duplicate", request.orderId(), payment.getStatus());
            throw new OperationException("Payment already processed for orderId: " + request.orderId(), HttpStatus.CONFLICT);
        }

        Payment payment = PaymentConverter.toEntity(request);
        repository.save(payment);
        log.info("Payment created with ID: {}", payment.getId());

        BankTransactionResponse bankTransactionResponse = bankServiceManager.startPayment(payment.getId(), payment.getAmount());
        payment.setStatus(PaymentStatus.STARTED);
        payment.setHtml(bankTransactionResponse.html());
        repository.save(payment);
        log.info("Payment started for payment ID: {}", payment.getId());
        return bankTransactionResponse.html();
    }


    public ModelAndView callback(PaymentCallbackRequest request) {
        Optional<Payment> optionalPayment = repository.findById(request.paymentId());
        if (optionalPayment.isEmpty()) {
            log.error("Payment not found for paymentId: {}", request.paymentId());
            return new ModelAndView("redirect:" + ERROR_URL).addObject(MESSAGE, "Payment not found");
        }
        Payment payment = optionalPayment.get();

        // check idempotency
        if (payment.getStatus() != PaymentStatus.STARTED) {
            log.warn("Payment with ID: {} is not in STARTED status. Current status: {}", payment.getId(), payment.getStatus());
            return new ModelAndView("redirect:" + ERROR_URL).addObject(MESSAGE, "Payment already processed");
        }

        boolean handshake = bankServiceManager.handshake(request.paymentId());
        if (!handshake) {
            log.error("Handshake failed for paymentId: {}", request.paymentId());
            return paymentStateManager.markAsFailed(request.paymentId());
        }

        return paymentStateManager.markAsCompleted(request.paymentId());
    }
}
