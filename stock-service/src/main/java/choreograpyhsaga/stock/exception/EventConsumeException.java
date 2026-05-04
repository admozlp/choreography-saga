package choreograpyhsaga.stock.exception;

import lombok.Getter;

@Getter
public class EventConsumeException extends RuntimeException {

    public EventConsumeException(String message, Throwable cause) {
        super(message, cause);
    }
}
