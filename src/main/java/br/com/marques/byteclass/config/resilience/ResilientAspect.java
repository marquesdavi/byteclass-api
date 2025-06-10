package br.com.marques.byteclass.config.resilience;

import br.com.marques.byteclass.common.exception.GenericException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.functions.CheckedSupplier;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Supplier;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ResilientAspect {

    private final RateLimiterRegistry rateLimiterRegistry;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Around("@annotation(resilient)")
    public Object applyResilience(ProceedingJoinPoint joinPoint,
                                  Resilient resilient) throws Throwable {

        RateLimiter rl = resolveRateLimiter(resilient.rateLimiter());
        CircuitBreaker cb = resolveCircuitBreaker(resilient.circuitBreaker());
        Supplier<Object> supplier = createDecoratedSupplier(joinPoint, rl, cb);

        try {
            return supplier.get();
        } catch (Exception wrapper) {
            Throwable cause = (wrapper.getCause() != null) ? wrapper.getCause() : wrapper;

            if (handleCause(joinPoint, resilient, cause)) return invokeFallback(joinPoint,
                    resilient.fallbackMethod(),
                    (Exception) cause);
            throw cause;
        }
    }

    private boolean handleCause(ProceedingJoinPoint joinPoint, Resilient resilient, Throwable cause) throws Throwable {
        if (cause instanceof RequestNotPermitted) {
            log.warn("RateLimiter blocked {}: {}",
                    joinPoint.getSignature(), cause.getMessage());
            throw cause;
        }
        if (cause instanceof GenericException) {
            throw cause;
        }

        return !resilient.fallbackMethod().isEmpty();
    }

    private Supplier<Object> createDecoratedSupplier(ProceedingJoinPoint jp,
                                                     RateLimiter rl,
                                                     CircuitBreaker cb) {
        CheckedSupplier<Object> checked = jp::proceed;

        if (cb != null) {
            checked = CircuitBreaker.decorateCheckedSupplier(cb, checked);
        }
        if (rl != null) {
            checked = RateLimiter.decorateCheckedSupplier(rl, checked);
        }

        final CheckedSupplier<Object> finalChecked = checked;

        return () -> {
            try {
                return finalChecked.get();
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        };
    }

    private RateLimiter resolveRateLimiter(String rateLimiterName) {
        return (Objects.nonNull(rateLimiterName) && !rateLimiterName.isEmpty())
                ? rateLimiterRegistry.rateLimiter(rateLimiterName)
                : null;
    }

    private CircuitBreaker resolveCircuitBreaker(String circuitBreakerName) {
        return (Objects.nonNull(circuitBreakerName) && !circuitBreakerName.isEmpty())
                ? circuitBreakerRegistry.circuitBreaker(circuitBreakerName)
                : null;
    }

    // From here, the methods were built to do custom fallbacks
    private Object invokeFallback(ProceedingJoinPoint joinPoint, String fallbackMethodName, Exception exception)
            throws Throwable {
        Object target = joinPoint.getTarget();
        Object[] originalArgs = joinPoint.getArgs();
        Object[] fallbackArgs = buildFallbackArguments(originalArgs, exception);

        Method fallbackMethod = findFallbackMethod(target, fallbackMethodName, fallbackArgs.length);
        if (Objects.nonNull(fallbackMethod)) {
            return fallbackMethod.invoke(target, fallbackArgs);
        } else {
            throw new IllegalStateException("Fallback method not found: " + fallbackMethodName);
        }
    }

    private Object[] buildFallbackArguments(Object[] originalArgs, Exception exception) {
        Object[] fallbackArgs = new Object[originalArgs.length + 1];
        System.arraycopy(originalArgs, 0, fallbackArgs, 0, originalArgs.length);
        fallbackArgs[fallbackArgs.length - 1] = exception;
        return fallbackArgs;
    }

    private Method findFallbackMethod(Object target, String fallbackMethodName, int fallbackArgsLength) {
        for (Method method : target.getClass().getMethods()) {
            if (method.getName().equals(fallbackMethodName)
                    && method.getParameterCount() == fallbackArgsLength) {
                return method;
            }
        }
        return null;
    }
}
