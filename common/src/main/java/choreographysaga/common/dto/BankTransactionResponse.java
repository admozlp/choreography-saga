package choreographysaga.common.dto;

import java.util.UUID;

public record BankTransactionResponse(
        String html,
        String paymentId,
        UUID signature
) {
}
