package choreographysaga.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OperationException extends RuntimeException {
    private final String message;
    private final HttpStatus httpStatus;

    public OperationException(String message, HttpStatus httpStatus) {
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
