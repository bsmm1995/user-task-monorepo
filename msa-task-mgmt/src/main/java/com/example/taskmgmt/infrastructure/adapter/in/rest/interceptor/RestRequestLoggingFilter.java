package com.example.taskmgmt.infrastructure.adapter.in.rest.interceptor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RestRequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RestRequestLoggingFilter.class);

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String MDC_TRACE_ID_KEY = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startNs = System.nanoTime();

        String traceId = Optional.ofNullable(request.getHeader(TRACE_ID_HEADER))
                .filter(s -> !s.isBlank())
                .orElseGet(() -> UUID.randomUUID().toString());

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        String pathWithQuery = (query == null || query.isBlank()) ? uri : (uri + "?" + query);
        String remoteAddr = request.getRemoteAddr();

        String userAgent = Optional.ofNullable(request.getHeader("User-Agent")).orElse("-");

        // Put it in MDC so any downstream logs can correlate.
        MDC.put(MDC_TRACE_ID_KEY, traceId);

        // Also echo it back so clients can correlate.
        response.setHeader(TRACE_ID_HEADER, traceId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - startNs) / 1_000_000;
            int status = response.getStatus();

            log.info("HTTP {} {} -> {} ({}ms) remoteAddr={} userAgent=\"{}\" {}={}",
                    method,
                    pathWithQuery,
                    status,
                    durationMs,
                    remoteAddr,
                    userAgent,
                    TRACE_ID_HEADER,
                    traceId);

            MDC.remove(MDC_TRACE_ID_KEY);
        }
    }
}
