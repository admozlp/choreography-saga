package choreographysaga.payment.exception;

import choreographysaga.common.dto.ApiResponse;
import choreographysaga.common.exception.OperationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OperationException.class)
    public ResponseEntity<ApiResponse<Void>> handleOperationException(OperationException e) {
        return ResponseEntity.status(e.getHttpStatus())
                .body(ApiResponse.error(e.getMessage(), e.getHttpStatus()));
    }
}
