package choreograpyhsaga.stock.listener.event;

public record StockReservationCompensationEvent(Long orderId, Long productId, Integer quantity) {
}
