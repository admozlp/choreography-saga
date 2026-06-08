package choreographysaga.common.event;

public class EventTypes {
    private EventTypes() {
        throw new IllegalStateException("Utility class");
    }

    public static final String ORDER_STATUS_UPDATE_FAILED_EVENT = "OrderStatusUpdateFailedEvent";
    public static final String PAYMENT_INITIATION_FAILED_EVENT = "PaymentInitiationFailedEvent";
    public static final String PAYMENT_FAILED_EVENT = "PaymentFailedEvent";
    public static final String PAYMENT_COMPLETED_EVENT = "PaymentCompletedEvent";
}
