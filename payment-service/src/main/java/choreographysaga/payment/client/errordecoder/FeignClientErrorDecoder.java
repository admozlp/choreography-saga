package choreographysaga.payment.client.errordecoder;

import choreographysaga.common.exception.CustomFeignException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;


@Slf4j
public class FeignClientErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = extractMessage(response, "Client error. Status: " + response.status());
        return switch (response.status()) {
            case 500, 502, 503, 504 -> new feign.RetryableException(
                    response.status(),
                    message,
                    response.request().httpMethod(),
                    (Long) null,
                    response.request()
            );
            default -> new CustomFeignException(
                    response.status(),
                    message,
                    null
            );
        };
    }

    private String extractMessage(Response response, String fallback) {
        if (response.body() == null) {
            return fallback;
        }
        try (InputStream is = response.body().asInputStream()) {
            JsonNode node = objectMapper.readTree(is);
            JsonNode messageNode = node.get("message");
            if (messageNode != null && !messageNode.isNull()) {
                return messageNode.asText();
            }
        } catch (IOException e) {
            log.warn("Failed to parse error response body: {}", e.getMessage());
        }
        return fallback;
    }
}
