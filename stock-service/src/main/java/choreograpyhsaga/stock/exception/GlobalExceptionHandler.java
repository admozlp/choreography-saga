package choreograpyhsaga.stock.exception;

import choreographysaga.common.dto.ApiResponse;
import jakarta.persistence.LockTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LockTimeoutException.class)
    public ApiResponse<String> handleLockTimeout(LockTimeoutException ex) {
        log.error("Lock timeout: {}", ex.getMessage());
        return ApiResponse.error("Sistem yoğun, lütfen tekrar deneyin.", HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(OperationException.class)
    public ApiResponse<Void> handleOperationException(OperationException ex) {
        return ApiResponse.error(ex.getMessage(), ex.getHttpStatus());
    }
}
