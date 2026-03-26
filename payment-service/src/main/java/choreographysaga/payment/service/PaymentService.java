package choreographysaga.payment.service;

import choreographysaga.common.dto.CreatePaymentRequest;
import choreographysaga.payment.converter.PaymentConverter;
import choreographysaga.payment.model.Payment;
import choreographysaga.payment.repository.PaymentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository repository;

    public String createPayment(@Valid CreatePaymentRequest request) {
        Payment payment = PaymentConverter.toEntity(request);

        // TODO: bank servise gidecek hata alırsa payment ın durumunu güncelleyecek ve order a da hata dönecek

        return null;
    }
}
