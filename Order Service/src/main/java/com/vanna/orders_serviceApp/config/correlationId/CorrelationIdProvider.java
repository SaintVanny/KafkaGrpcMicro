package com.vanna.orders_serviceApp.config.correlationId;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class CorrelationIdProvider {//

    public UUID getCurrentCorrelationId() {
        String correlationId = MDC.get(LoggingConstants.CORRELATION_ID);

        if (correlationId == null || correlationId.isBlank()) {
            return UUID.randomUUID();
        }

        return UUID.fromString(correlationId);
    }
}