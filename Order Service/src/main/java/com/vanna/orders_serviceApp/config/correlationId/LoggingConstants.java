package com.vanna.orders_serviceApp.config.correlationId;

public final class LoggingConstants {

    public static final String CORRELATION_ID = "correlationId";

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    private LoggingConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
}