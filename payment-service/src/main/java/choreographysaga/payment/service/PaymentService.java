package choreographysaga.payment.service;

import choreographysaga.common.dto.BankResponse;
import choreographysaga.common.dto.CreatePaymentRequest;
import choreographysaga.common.exception.OperationException;
import choreographysaga.payment.client.BankService;
import choreographysaga.payment.converter.PaymentConverter;
import choreographysaga.payment.model.Payment;
import choreographysaga.payment.model.enm.PaymentStatus;
import choreographysaga.payment.repository.PaymentRepository;
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

        // TODO: bank servise gidecek hata alırsa payment ın durumunu güncelleyecek ve order a da hata dönecek
        try {
            BankResponse bankResponse = bankService.startPayment(payment.getId(), payment.getAmount());
            log.info("Bank request successfull, paymentId: {}", bankResponse.paymentId());
            return bankResponse.html();
        } catch (Exception e) {
            log.error("Bank request error, paymentId: {}", payment.getId());
            payment.setStatus(PaymentStatus.FAILED);
            repository.save(payment);
            throw new OperationException("Bank Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
