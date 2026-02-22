package com.example.taskmgmt.infrastructure.adapter.out.client.interceptor;

import com.example.taskmgmt.infrastructure.util.LogConstants;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ExternalApiLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ExternalApiLoggingInterceptor.class);

    @Override
    @Nonnull
    public ClientHttpResponse intercept(@Nonnull HttpRequest request, @Nonnull byte[] body, @Nonnull ClientHttpRequestExecution execution) throws IOException {
        long startNs = System.nanoTime();
        String traceId = MDC.get(LogConstants.MDC_TRACE_ID_KEY);

        if (traceId != null && !traceId.isBlank()) {
            request.getHeaders().add(LogConstants.TRACE_ID_HEADER, traceId);
        }

        String method = request.getMethod().name();
        String uri = request.getURI().toString();

        log.info(">>> External Request: HTTP {} {} (traceId={})",
                method,
                uri,
                traceId != null ? traceId : "-");

        try {
            ClientHttpResponse response = execution.execute(request, body);
            long durationMs = (System.nanoTime() - startNs) / 1_000_000;
            int status = response.getStatusCode().value();

            log.info("<<< External Response: HTTP {} {} -> {} ({}ms) (traceId={})",
                    method,
                    uri,
                    status,
                    durationMs,
                    traceId != null ? traceId : "-");

            return response;
        } catch (Exception e) {
            long durationMs = (System.nanoTime() - startNs) / 1_000_000;
            log.error("<<< External Response: HTTP {} {} -> ERROR: {} ({}ms) (traceId={})",
                    method,
                    uri,
                    e.getMessage(),
                    durationMs,
                    traceId != null ? traceId : "-");
            throw e;
        }
    }
}
