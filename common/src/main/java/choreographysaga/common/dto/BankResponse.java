package choreographysaga.common.dto;

public record BankResponse(
        String html,
        String paymentId,
        String signature
) {
}
