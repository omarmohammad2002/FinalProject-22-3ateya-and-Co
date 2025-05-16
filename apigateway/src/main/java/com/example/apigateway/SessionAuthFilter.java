//package com.example.apigateway;
//
//import com.example.apigateway.UserClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpCookie;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//@Component
//@Order(1)
//public class SessionAuthFilter implements GlobalFilter {
//
////    @Autowired
//    private UserClient userClient;
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        String path = exchange.getRequest().getPath().toString();
//
//        // Skip authentication for login and register endpoints
//        if (path.startsWith("/user/login") || path.startsWith("/user/register")) {
//            return chain.filter(exchange);
//        }
//
//        HttpCookie sessionCookie = exchange.getRequest().getCookies().getFirst("SESSIONID");
//
//        if (sessionCookie == null) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        String sessionId = sessionCookie.getValue();
//
//        try {
//            boolean isValid = userClient.validateSession(sessionId);
//            if (!isValid) {
//                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                return exchange.getResponse().setComplete();
//            }
//        } catch (Exception e) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        return chain.filter(exchange);
//    }
//}
