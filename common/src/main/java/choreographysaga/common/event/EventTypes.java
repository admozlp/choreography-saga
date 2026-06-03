package choreographysaga.common.event;

public class EventTypes {
    private EventTypes() {
        throw new IllegalStateException("Utility class");
    }

    public static final String STOCK_RESERVATION_COMPENSATION_EVENT = "StockReservationCompensationEvent";
    public static final String PAYMENT_FAILED_EVENT = "PaymentFailedEvent";
}
