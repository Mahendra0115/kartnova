package com.kartnova.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // Original incoming request
        ServerHttpRequest request = exchange.getRequest();

        // IMPORTANT:
        // Har request ko unique request id de rahe hain
        // Future tracing aur debugging ke liye useful
        String requestId = generateRequestId();

        // IMPORTANT:
        // Downstream services ko same request id bhejne ke liye header add kar rahe hain
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(REQUEST_ID_HEADER, requestId)
                .build();

        // IMPORTANT:
        // Mutated request ko exchange me set karna zaroori hota hai
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        // Incoming request log
        log.info("Incoming Request -> requestId={}, method={}, path={}",
                requestId,
                mutatedRequest.getMethod(),
                mutatedRequest.getURI().getPath());

        return chain.filter(mutatedExchange)
                .doOnSuccess(aVoid -> {
                    String status = mutatedExchange.getResponse().getStatusCode() != null
                            ? mutatedExchange.getResponse().getStatusCode().toString()
                            : "UNKNOWN";

                    log.info("Outgoing Response -> requestId={}, status={}",
                            requestId,
                            status);
                })
                .doOnError(ex -> {
                    log.error("Request Failed -> requestId={}, errorMessage={}",
                            requestId,
                            ex.getMessage(),
                            ex);
                });
    }

    @Override
    public int getOrder() {
        // IMPORTANT:
        // Lower order means filter chain me jaldi execute hoga
        return -1;
    }

    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }
}