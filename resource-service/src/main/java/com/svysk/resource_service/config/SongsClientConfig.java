package com.svysk.resource_service.config;

import com.svysk.openapi.ApiClient;
import com.svysk.openapi.client.SongsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class SongsClientConfig {

    @Bean
    public RestClient restClient( @Value("${song-srv.url}") String url) {

        return RestClient.builder()
                .baseUrl(url)
                .build();
    }

    @Bean
    public ApiClient apiClient(RestClient restClient) {
        return new ApiClient(restClient);
    }

    @Bean
    public SongsApi songsApi(ApiClient apiClient) {
        return new SongsApi(apiClient);
    }
}
