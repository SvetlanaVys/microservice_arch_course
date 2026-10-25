package com.svysk.resource_processor.config;

import com.svysk.openapi.ApiClient;
import com.svysk.openapi.client.SongsApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class SongsClientConfig {

    @Value("${song-srv.url}")
    String url;

    @Bean
    @Qualifier("songRestClient")
    public RestClient restClient(
            @Qualifier("loadBalancedRestClientBuilder")
            RestClient.Builder restClientBuilder) {

        return restClientBuilder
                .baseUrl(url)
                .build();
    }

    @Bean
    @Qualifier("songRestClient")
    public ApiClient apiClient(
            @Qualifier("songRestClient") RestClient restClient) {
        ApiClient apiClient = new ApiClient(restClient);
        apiClient.setBasePath(url);

        return apiClient;
    }

    @Bean
    public SongsApi songsApi(
            @Qualifier("songRestClient") ApiClient apiClient) {
        return new SongsApi(apiClient);
    }
}
