package choreographysaga.payment.converter;

import choreographysaga.common.dto.CreatePaymentRequest;
import choreographysaga.payment.model.Payment;

public class PaymentConverter {
    private PaymentConverter() {
    }


    public static Payment toEntity(CreatePaymentRequest request) {
        return Payment.builder()
                .orderId(request.orderId())
                .amount(request.amount())
                .build();
    }
}
