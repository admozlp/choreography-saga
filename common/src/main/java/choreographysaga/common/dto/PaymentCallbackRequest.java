package choreographysaga.common.dto;

import choreographysaga.common.model.enm.BankTransactionStatus;
import jakarta.validation.constraints.NotNull;

public record PaymentCallbackRequest(
        @NotNull(message = "Payment ID cannot be null")
        Long paymentId,

        @NotNull(message = "Status cannot be null")
        BankTransactionStatus status

) {
}
