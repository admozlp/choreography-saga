package choreographysaga.payment.exception;

import choreographysaga.common.dto.ApiResponse;
import choreographysaga.common.exception.OperationException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.HttpStatus;
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

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<ApiResponse<Void>> handleCallNotPermittedException(CallNotPermittedException e) {
        return ResponseEntity.status(503)
                .body(ApiResponse.error("Service unavailable, please try later", HttpStatus.SERVICE_UNAVAILABLE));
    }

    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<ApiResponse<Void>> handleClassCastException(ClassCastException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
