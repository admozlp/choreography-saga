package choreographysaga.common.dto;

import jakarta.validation.constraints.NotNull;

public record BankHandshakeRequest(
        @NotNull(message = "paymentId must not be null")
        Long paymentId
) {

}
