package choreographysaga.order.client.errordecoder;

import feign.Response;
import feign.codec.ErrorDecoder;

public class ClientErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 500, 502, 503, 504 -> new feign.RetryableException(
                    response.status(),
                    "Client error. Status: " + response.status(),
                    response.request().httpMethod(),
                    (Long) null,
                    response.request()
            );
            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}
