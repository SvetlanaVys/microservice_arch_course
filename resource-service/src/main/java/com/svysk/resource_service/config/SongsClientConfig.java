package com.svysk.resource_service.config;

import com.svysk.openapi.ApiClient;
import com.svysk.openapi.client.SongsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class SongsClientConfig {

    @Value("${song-srv.url}")
    String url;

    @Bean
    public RestClient restClient() {

        return RestClient.builder()
                .baseUrl(url)
                .build();
    }

    @Bean
    public ApiClient apiClient(RestClient restClient) {
        ApiClient apiClient = new ApiClient(restClient);
        apiClient.setBasePath(url);

        return apiClient;
    }

    @Bean
    public SongsApi songsApi(ApiClient apiClient) {
        return new SongsApi(apiClient);
    }
}
