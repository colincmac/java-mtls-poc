package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class LegacyApiService {

    private final WebClient webClientWithMtls;

    @Autowired
    public LegacyApiService(WebClient webClientWithMtls) {
        this.webClientWithMtls = webClientWithMtls;
    }

    public Mono<String> callLegacyApi(String endpoint) {
        return webClientWithMtls
                .get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(String.class);
    }
}
