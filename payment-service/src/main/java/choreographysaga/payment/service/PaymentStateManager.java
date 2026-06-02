package choreographysaga.payment.service;


import choreographysaga.payment.model.enm.PaymentStatus;
import choreographysaga.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import static choreographysaga.payment.util.Constant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentStateManager {
    private final PaymentRepository repository;


    @Transactional
    public ModelAndView markAsFailed(Long paymentId) {
        int updateCount = repository.updateIfStatusStarted(paymentId, PaymentStatus.FAILED);
        if (updateCount == 0) {
            log.error("Payment already processed in markAsFailed with paymentId: {}", paymentId);
            return new ModelAndView(ERROR, REDIRECT_URL, ERROR_URL).addObject(MESSAGE, "Payment already processed");
        }

        //events

        return new ModelAndView(ERROR, REDIRECT_URL, ERROR_URL).addObject(MESSAGE, "Payment failed");
    }

    @Transactional
    public ModelAndView markAsCompleted(Long paymentId) {
        int updateCount = repository.updateIfStatusStarted(paymentId, PaymentStatus.COMPLETED);
        if (updateCount == 0) {
            log.error("Payment already processed in markAsCompleted, with paymentId: {}", paymentId);
            return new ModelAndView(ERROR, REDIRECT_URL, ERROR_URL).addObject(MESSAGE, "Payment already processed");
        }

        //events

        return new ModelAndView(SUCCESS, REDIRECT_URL, SUCCESS_URL).addObject(MESSAGE, "Payment successfully completed");
    }
}
