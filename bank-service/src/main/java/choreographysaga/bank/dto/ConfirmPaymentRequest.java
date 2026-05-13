package choreographysaga.bank.dto;

import jakarta.validation.constraints.NotNull;

public record ConfirmPaymentRequest(
        @NotNull(message = "paymentId cannot be null")
        Long paymentId,
        @NotNull(message = "otpCode cannot be null")
        Integer otpCode
) {
}
