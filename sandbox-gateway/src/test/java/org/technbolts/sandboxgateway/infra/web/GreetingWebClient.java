package org.technbolts.sandboxgateway.infra.web;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GreetingWebClient {

    public static void main(String[] args) {
        WebClient client = WebClient.create("http://localhost:8080");

        Mono<ClientResponse> result = client.get()
                .uri("/hello")
                .accept(MediaType.TEXT_PLAIN)
                .exchange();

        String content = result.flatMap(res -> res.bodyToMono(String.class)).block();
        System.out.println("result>> '" + content + "'");
    }
}