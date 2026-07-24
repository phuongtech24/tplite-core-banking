package com.tplite.core_banking.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceExecutionLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(ServiceExecutionLoggingAspect.class);

    @Around("execution(* com.tplite.core_banking.module..service.impl..*(..))")
    public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long startedAt = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        log.info("Service method started: method={}", methodName);
        try {
            Object result = joinPoint.proceed();
            long durationMs = System.currentTimeMillis() - startedAt;
            log.info("Service method completed: method={}, status=SUCCESS, durationMs={}", methodName, durationMs);
            return result;
        } catch (Throwable ex) {
            long durationMs = System.currentTimeMillis() - startedAt;
            log.warn("Service method failed: method={}, status=FAILED, exception={}, durationMs={}",
                    methodName, ex.getClass().getSimpleName(), durationMs);
            throw ex;
        }
    }
}
