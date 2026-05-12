package choreographysaga.common.exception;

import feign.FeignException;

public class CustomFeignException extends FeignException {
    public CustomFeignException(int status, String message, Throwable cause) {
        super(status, message, cause);
    }
}
