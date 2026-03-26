package choreographysaga.common.dto;


import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreatePaymentRequest(
        @NotNull(message = "orderId cannot be null")
        Long orderId,
        @NotNull(message = "amount cannot be null")
        BigDecimal amount
) {
}
