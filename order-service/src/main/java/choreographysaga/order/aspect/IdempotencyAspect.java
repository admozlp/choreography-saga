package choreographysaga.order.aspect;

import choreographysaga.order.aspect.annotation.Idempotent;
import choreographysaga.order.model.IdempotencyKey;
import choreographysaga.order.service.IdempotencyService;
import choreographysaga.order.util.HashUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Type;
import java.util.Optional;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {

    private static final String HEADER_NAME = "Idempotency-Key";

    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(idempotent)")
    public Object handleIdempotency(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        String key = currentRequest().getHeader(HEADER_NAME);
        if (key == null || key.isBlank()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Idempotency-Key header is required");
        }

        String operation = idempotent.operation();
        String requestHash = HashUtil.sha256(objectMapper.writeValueAsString(joinPoint.getArgs()));

        Optional<IdempotencyKey> existing = idempotencyService.findByKeyAndOperation(key, operation);
        if (existing.isPresent()) {
            return handleExisting(existing.get(), requestHash, joinPoint);
        }

        try {
            idempotencyService.claim(key, operation, requestHash);
        } catch (DataIntegrityViolationException e) {
            log.debug("Race on claim for key={}, operation={} — re-checking", key, operation);
            return handleIdempotency(joinPoint, idempotent);
        }

        try {
            Object result = joinPoint.proceed();
            idempotencyService.complete(key, operation, HttpStatus.OK.value(),
                    objectMapper.writeValueAsString(result));
            return result;
        } catch (Throwable t) {
            idempotencyService.fail(key, operation, HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw t;
        }
    }

    private Object handleExisting(IdempotencyKey rec, String hash, ProceedingJoinPoint joinPoint) {
        if (!rec.getRequestHash().equals(hash)) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(422),
                    "Idempotency key reused with different request body");
        }
        return switch (rec.getStatus()) {
            case COMPLETED -> deserialize(rec.getResponse(), joinPoint);
            case IN_PROGRESS -> throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Request already in progress for this idempotency key");
            case FAILED -> throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Previous attempt failed, please use a new idempotency key");
        };
    }

    private Object deserialize(String json, ProceedingJoinPoint joinPoint) {
        Type returnType = ((MethodSignature) joinPoint.getSignature()).getMethod().getGenericReturnType();
        JavaType javaType = objectMapper.getTypeFactory().constructType(returnType);
        return objectMapper.readValue(json, javaType);
    }

    private HttpServletRequest currentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }
}
