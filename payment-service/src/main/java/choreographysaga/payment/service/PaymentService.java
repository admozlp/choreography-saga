package choreographysaga.payment.service;

import choreographysaga.common.dto.BankResponse;
import choreographysaga.common.dto.CreatePaymentRequest;
import choreographysaga.common.exception.OperationException;
import choreographysaga.payment.client.BankService;
import choreographysaga.payment.converter.PaymentConverter;
import choreographysaga.payment.model.Payment;
import choreographysaga.payment.model.enm.PaymentStatus;
import choreographysaga.payment.repository.PaymentRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository repository;
    private final BankService bankService;

    public String createPayment(CreatePaymentRequest request) {
        Payment payment = PaymentConverter.toEntity(request);
        repository.save(payment);

        try {
            BankResponse bankResponse = bankService.startPayment(payment.getId(), payment.getAmount());
            log.info("Bank request successfull, paymentId: {}", bankResponse.paymentId());
            return bankResponse.html();
        } catch (RuntimeException e) {
            payment.setStatus(PaymentStatus.FAILED);
            repository.save(payment);
            HttpStatus status = (e instanceof FeignException fe && fe.status() > 0) ? HttpStatus.valueOf(fe.status()) : HttpStatus.INTERNAL_SERVER_ERROR;

            log.error("Bank request error, paymentId: {}, httpStatusCode: {}", payment.getId(), status.value());
            throw new OperationException("Bank Error. Couldn't start payment", status);
        }
    }
}
