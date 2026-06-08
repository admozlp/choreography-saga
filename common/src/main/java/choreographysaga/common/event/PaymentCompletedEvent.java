package choreographysaga.common.event;

public record PaymentCompletedEvent(Long orderId, Long paymentId) {
}
