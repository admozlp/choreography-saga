package choreographysaga.payment.service;

import choreographysaga.common.dto.BankResponse;
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


    public ModelAndView callback(PaymentCallbackRequest request) {
        Optional<Payment> optionalPayment = repository.findById(request.paymentId());
        if(optionalPayment.isEmpty()) {
            log.error("Payment not found for paymentId: {}", request.paymentId());
            return new ModelAndView("error", "redirectUrl", "http://localhost:3530/payments/error");
        }
        Payment payment = optionalPayment.get();


        // check idempotency
        // use handshake method to verify the connection between bank and payment service
        // update payment status
        // publish events: order, stock, bank, notification
        // redirect

        return new ModelAndView("success", "redirectUrl", "http://localhost:3530/payments/success");
    }
}
