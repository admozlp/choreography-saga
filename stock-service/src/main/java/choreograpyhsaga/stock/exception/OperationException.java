package choreograpyhsaga.stock.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OperationException extends RuntimeException {
    private String message;
    private HttpStatus httpStatus;

    public OperationException(String message, HttpStatus httpStatus) {
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
