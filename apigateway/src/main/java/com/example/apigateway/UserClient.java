//package com.example.apigateway;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@FeignClient(name = "user-service", url = "http://user-service:8080/userapi/sessions")
//public interface UserClient {
//    @GetMapping("/validate")
//    boolean validateSession(@RequestParam("sessionId") int sessionId);
//}
//

package com.example.apigateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserClient {

    private final WebClient webClient;

    @Autowired
    public UserClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://user-service:8080/userapi/sessions").build();
    }

    public Mono<Boolean> validateSession(int sessionId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/validate")
                        .queryParam("sessionId", sessionId)
                        .build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(e -> {
                    // Log or handle error, but return false to indicate failure
                    return Mono.just(false);
                });
    }
}
