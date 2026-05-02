package choreographysaga.order.listener.event;

public record StockReservedEvent(Long orderId, Long productId, Integer quantity) {
}
