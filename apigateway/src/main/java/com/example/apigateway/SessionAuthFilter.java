////package com.example.apigateway;
////
////import org.slf4j.Logger;
////import org.slf4j.LoggerFactory;
////import org.springframework.beans.factory.ObjectProvider;
////import org.springframework.cloud.gateway.filter.GlobalFilter;
////import org.springframework.cloud.gateway.filter.GatewayFilterChain;
////import org.springframework.core.annotation.Order;
////import org.springframework.http.HttpCookie;
////import org.springframework.http.HttpStatus;
////import org.springframework.stereotype.Component;
////import org.springframework.web.server.ServerWebExchange;
////import reactor.core.publisher.Mono;
////
////@Component
////@Order(1)
////public class SessionAuthFilter implements GlobalFilter {
////
////    private static final Logger logger = LoggerFactory.getLogger(SessionAuthFilter.class);
////
////    private final ObjectProvider<UserClient> userClientProvider;
////
////    public SessionAuthFilter(ObjectProvider<UserClient> userClientProvider) {
////        this.userClientProvider = userClientProvider;
////    }
////
////    @Override
////    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
////        String path = exchange.getRequest().getPath().toString();
////        logger.info("Intercepted request to path: {}", path);
////
////        // Skip authentication for login and register endpoints
////        if (path.startsWith("/userapi/sessions/login") || path.startsWith("/userapi/user/register")) {
////            logger.info("Bypassing authentication for path: {}", path);
////            return chain.filter(exchange);
////        }
////
////        HttpCookie sessionCookie = exchange.getRequest().getCookies().getFirst("SESSION_ID");
////
////        if (sessionCookie == null) {
////            logger.warn("SESSION_ID cookie not found. Rejecting with 401.");
////            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
////            return exchange.getResponse().setComplete();
////        }
////
////        int sessionId;
////        try {
////            sessionId = Integer.parseInt(sessionCookie.getValue());
////            logger.info("SESSION_ID found: {}", sessionId);
////        } catch (NumberFormatException e) {
////            logger.error("Invalid SESSION_ID format: {}", sessionCookie.getValue(), e);
////            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
////            return exchange.getResponse().setComplete();
////        }
////
////        return Mono.fromCallable(() -> {
////                    UserClient userClient = userClientProvider.getIfAvailable();
////                    if (userClient == null) {
////                        logger.error("UserClient is not available.");
////                        throw new IllegalStateException("UserClient bean not available");
////                    }
////                    boolean isValid = userClient.validateSession(sessionId);
////                    logger.info("Session validation result for ID {}: {}", sessionId, isValid);
////                    return isValid;
////                })
////                .flatMap(valid -> {
////                    if (!valid) {
////                        logger.warn("Session {} is invalid. Rejecting with 401.", sessionId);
////                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
////                        return exchange.getResponse().setComplete();
////                    }
////                    logger.info("Session {} is valid. Continuing filter chain.", sessionId);
////                    return chain.filter(exchange);
////                })
////                .onErrorResume(e -> {
////                    logger.error("Error during session validation: {}", e.getMessage(), e);
////                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
////                    return exchange.getResponse().setComplete();
////                });
////    }
////}
//package com.example.apigateway;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpCookie;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//@Component
//@Order(1)
//public class SessionAuthFilter implements GlobalFilter {
//
//    private static final Logger logger = LoggerFactory.getLogger(SessionAuthFilter.class);
//
//    private final UserClient userClient;
//
//    public SessionAuthFilter(UserClient userClient) {
//        this.userClient = userClient;
//    }
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        String path = exchange.getRequest().getPath().toString();
//        logger.info("Intercepted request to path: {}", path);
//
//        if (path.startsWith("/userapi/sessions/login") || path.startsWith("/userapi/user/register")) {
//            logger.info("Bypassing authentication for path: {}", path);
//            return chain.filter(exchange);
//        }
//
//        HttpCookie sessionCookie = exchange.getRequest().getCookies().getFirst("SESSION_ID");
//
//        if (sessionCookie == null) {
//            logger.warn("SESSION_ID cookie not found. Rejecting with 401.");
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        int sessionId;
//        try {
//            sessionId = Integer.parseInt(sessionCookie.getValue());
//            logger.info("SESSION_ID found: {}", sessionId);
//        } catch (NumberFormatException e) {
//            logger.error("Invalid SESSION_ID format: {}", sessionCookie.getValue(), e);
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        return userClient.validateSession(sessionId)
//                .flatMap(isValid -> {
//                    if (!isValid) {
//                        logger.warn("Session {} is invalid. Rejecting with 401.", sessionId);
//                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                        return exchange.getResponse().setComplete();
//                    }
//                    logger.info("Session {} is valid. Continuing filter chain.", sessionId);
//                    return chain.filter(exchange);
//                })
//                .onErrorResume(e -> {
//                    logger.error("Error during session validation: {}", e.getMessage(), e);
//                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                    return exchange.getResponse().setComplete();
//                });
//    }
//}
