package com.vanna.orders_serviceApp.config.correlationId;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String correlationId = generateCorrelationId(request);

        MDC.put(LoggingConstants.CORRELATION_ID, correlationId);

        response.addHeader(LoggingConstants.CORRELATION_ID_HEADER, correlationId);

        long startTime = System.currentTimeMillis();

        try {
            log.info("Incoming request: method={}, uri={}, correlationId={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    correlationId);

            filterChain.doFilter(request, response);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Request completed: method={}, uri={}, status={}, duration={}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Request failed: method={}, uri={}, duration={}ms, error={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    duration,
                    e.getMessage(),
                    e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    private String generateCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader(LoggingConstants.CORRELATION_ID_HEADER);

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        return correlationId;
    }

}