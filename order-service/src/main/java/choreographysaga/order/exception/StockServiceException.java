package choreographysaga.order.exception;

import lombok.Getter;

@Getter
public class StockServiceException extends RuntimeException {
    private final String message;

    public StockServiceException(String s, Throwable cause) {
        super(s, cause);
        this.message = s;
    }
}