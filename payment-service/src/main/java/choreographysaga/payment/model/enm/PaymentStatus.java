package choreographysaga.payment.model.enm;


import lombok.Getter;

@Getter
public enum PaymentStatus {
    CREATED("Created"),
    STARTED("Started"),
    COMPLETED("Completed"),
    FAILED("Failed");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }
}
