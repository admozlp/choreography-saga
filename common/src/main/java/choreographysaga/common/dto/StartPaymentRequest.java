package choreographysaga.common.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record StartPaymentRequest(
        @NotNull(message = "paymentId cannot be null")
        Long paymentId,
        @NotNull(message = "amount cannot be null")
        BigDecimal amount
) {
}
