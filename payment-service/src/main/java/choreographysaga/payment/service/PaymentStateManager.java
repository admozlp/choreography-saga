package choreographysaga.payment.service;


import choreographysaga.common.event.PaymentFailedEvent;
import choreographysaga.payment.model.Outbox;
import choreographysaga.payment.model.Payment;
import choreographysaga.payment.model.enm.PaymentStatus;
import choreographysaga.payment.repository.PaymentRepository;
import jakarta.persistence.LockTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

import static choreographysaga.common.event.EventTypes.PAYMENT_FAILED_EVENT;
import static choreographysaga.payment.util.Constant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentStateManager {
    private final PaymentRepository repository;
    private final OutboxEventPublisher outboxEventPublisher;
    private final ObjectMapper objectMapper;


    @Retryable(value = {LockTimeoutException.class}, maxRetries = 3, delay = 200)
    @Transactional
    public ModelAndView markAsFailed(Long paymentId) {
        Optional<Payment> optionalPayment = repository.findByIdForUpdate(paymentId);
        if (optionalPayment.isEmpty()) {
            log.error("Payment not found in markAsFailed for paymentId: {}", paymentId);
            return new ModelAndView(REDIRECT + ERROR_URL).addObject(MESSAGE, "Payment not found");
        }
        Payment payment = optionalPayment.get();
        if (payment.getStatus() != PaymentStatus.STARTED) {
            log.error("Payment already processed in markAsFailed with paymentId: {}", paymentId);
            return new ModelAndView(REDIRECT + ERROR_URL).addObject(MESSAGE, "Payment already processed");
        }
        payment.setStatus(PaymentStatus.FAILED);

        //events
        outboxEventPublisher.push(
                new Outbox(paymentId.toString(), "Payment", PAYMENT_FAILED_EVENT,
                        objectMapper.writeValueAsString(new PaymentFailedEvent(payment.getOrderId())
                        )));

        return new ModelAndView(REDIRECT + ERROR_URL).addObject(MESSAGE, "Payment failed");
    }

    @Transactional
    public ModelAndView markAsCompleted(Long paymentId) {
        int updateCount = repository.updateIfStatusStarted(paymentId, PaymentStatus.COMPLETED);
        if (updateCount == 0) {
            log.error("Payment already processed in markAsCompleted, with paymentId: {}", paymentId);
            return new ModelAndView(REDIRECT + ERROR_URL).addObject(MESSAGE, "Payment already processed");
        }

        //events

        return new ModelAndView(REDIRECT + SUCCESS_URL);
    }
}
