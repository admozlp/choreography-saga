package choreograpyhsaga.stock.model.enm;

import lombok.Getter;

@Getter
public enum ReserveStockStatus {
    RESERVED("Reserved"),
    CONFIRMED("Confirmed"),
    CANCELED("Canceled"),
    EXPIRED("Expired");

    private final String displayName;

    ReserveStockStatus(String displayName) {
        this.displayName = displayName;
    }
}
